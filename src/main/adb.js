const {ipcMain} = require('electron')
const fs = require('fs')
const client = require('adbkit').createClient()

export default class ADB {
  constructor () {
    ipcMain.on('list-devices', async (event) => {
      const devices = await client.listDevices()
      for (let i = 0; i < devices.length; i++) {
        let it = devices[i]
        const prop = await client.getProperties(it.id)
        it.sdkVersion = prop['ro.build.version.sdk']
        it.sysVersion = prop['ro.build.version.release']
        it.brand = prop['ro.product.brand']
        it.model = prop['ro.product.model']
        it.imei = prop['persist.radio.imei']
      }

      event.sender.send('list-devices-rsp', devices)
    })

    ipcMain.on('list-files', async (event, args) => {
      const files = await client.readdir(args.id, args.path)
      let newFiles = []
      files.forEach(function (file, index) {
        newFiles.push({
          name: file.name,
          path: args.path + '/' + file.name,
          size: file.size,
          mtime: file.mtime,
          isDirectory: file.isDirectory()
        })
      })

      if (!args.showHiddenFiles) {
        newFiles = newFiles.filter(it => {
          return it.name.indexOf('.') !== 0
        })
      }

      if (args.onlyShowFile) {
        newFiles = newFiles.filter(it => {
          return !it.isDirectory
        })
      }

      newFiles = newFiles.map((it, index) => {
        it.index = index
        return it
      })

      event.sender.send('list-files-rsp', newFiles)
    })

    ipcMain.on('pull-files', async (event, args) => {
      const dir = args.parent
      const items = args.items
      items.forEach(function (it) {
        client.pull(args.id, it.path).then(function (transfer) {
          const outputPath = args.outputDir + it.path.replace(dir, '')
          transfer.on('progress', function (stats) {
            event.sender.send('pull-file-progress', {
              index: it.index,
              path: it.path,
              progress: parseInt((stats.bytesTransferred / it.size).toFixed(2) * 100)
            })
          })
          transfer.on('end', function () {
            event.sender.send('pull-file-success', outputPath)
          })
          transfer.on('error', function () {
            event.sender.send('pull-file-error', outputPath)
          })
          console.info('导出文件', outputPath)
          transfer.pipe(fs.createWriteStream(outputPath))
        })
      })
    })
  }
}
