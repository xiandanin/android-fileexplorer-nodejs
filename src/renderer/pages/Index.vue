<template>
    <div class="container">
        <div class="header">
            <el-select v-model="current.id" placeholder="选择设备" size="small" @change="handleDeviceChanged">
                <el-option v-for="it in devices" :label="formatDeviceLabel(it)" :value="it.id"
                           v-bind:key="it.id"></el-option>
            </el-select>
            <el-button class="btn-update" size="small" @click="handleUpdateDevices">刷新</el-button>
            <i class="separate"></i>
            <div>
                <el-button size="small" @click="handleClickDownload(selectionFiles)">导出</el-button>
            </div>
        </div>

        <el-row class="breadcrumb-row">
            <el-col :span="14">
                <el-breadcrumb separator="/" class="breadcrumb">
                    <el-breadcrumb-item v-for="(it,index) in current.pathArray">
                        <el-link v-if="index<current.pathArray.length-1" type="primary" :underline="false"
                                 @click="handleFilesChanged(it.path)">
                            <span>{{it.name}}</span>
                        </el-link>
                        <span v-else>{{it.name}}</span>
                    </el-breadcrumb-item>
                </el-breadcrumb>
            </el-col>

            <el-col :span="10" style="text-align: right">
                <el-checkbox v-model="current.onlyShowFile" @change="handleFilesOptionsChanged('onlyShowFile',$event)">
                    仅显示文件
                </el-checkbox>
                <el-checkbox v-model="current.showHiddenFiles"
                             @change="handleFilesOptionsChanged('showHiddenFiles',$event)">
                    显示隐藏文件
                </el-checkbox>
            </el-col>
        </el-row>

        <div v-if="files">
            <el-table
                    size="small"
                    :data="files"
                    border
                    @selection-change="handleSelectionChange"
                    style="width: 100%">
                <el-table-column
                        type="selection"
                        width="35"></el-table-column>
                <el-table-column
                        sortable
                        prop="name"
                        label="名称">
                    <template slot-scope="scope">
                        <el-link type="primary" :underline="false" v-if="scope.row.isDirectory"
                                 @click="handleFilesChanged(scope.row.path)">
                            <i class="iconfont icon-icon-test"></i>
                            <span>{{scope.row.name}}</span>
                        </el-link>
                        <div v-else>
                            <i class="iconfont icon-wenjian"></i>
                            <span>{{scope.row.name}}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column
                        sortable
                        sort-by="size"
                        prop="size"
                        :formatter="formatSize"
                        label="大小"
                        align="right"
                        width="100">
                </el-table-column>
                <el-table-column
                        sortable
                        align="center"
                        sort-by="mtime"
                        :formatter="formatDateTime"
                        prop="mtime"
                        label="修改日期"
                        width="160">
                </el-table-column>
                <el-table-column
                        label="操作"
                        align="center"
                        width="180">
                    <template slot-scope="scope">
                        <el-button-group>
                            <el-button size="mini" type="button" @click="handleClickCopy(scope.row.path)">复制路径
                            </el-button>
                            <el-button size="mini" type="button" @click="handleClickDownload(scope.row)"
                                       :loading="scope.row.progress>0&&scope.row.progress<100">
                                {{scope.row.progress>0&&scope.row.progress<100?scope.row.progress+'%':'导出'}}
                            </el-button>
                        </el-button-group>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script>
  import {ipcRenderer, clipboard} from 'electron'
  import moment from 'moment'

  export default {
    data () {
      return {
        devices: [],
        files: null,
        selectionFiles: [],
        current: {
          id: null,
          onlyShowFile: false,
          showHiddenFiles: false,
          path: 'sdcard',
          pathArray: [{
            name: 'sdcard',
            path: 'sdcard'
          }]
        }
      }
    },
    methods: {
      formatDeviceLabel (it) {
        return `${it.id} - ${it.brand} ${it.model} - Android ${it.sysVersion} ( ${it.sdkVersion} ) - ${it.imei}`
      },
      formatSize (row, column, length) {
        if (row.isDirectory) {
          return ''
        }
        const unitArr = ['B ', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
        if (length <= 0) {
          return '0 ' + unitArr[0]
        }
        const index = Math.floor(Math.log(length) / Math.log(1024))
        const size = length / Math.pow(1024, index)
        return size.toFixed(2) + ' ' + unitArr[index]
      },
      formatDateTime (row, column, date) {
        return moment(date).format('YYYY-MM-DD HH:mm:ss')
      },
      handleDeviceChanged (deviceId) {
        console.info('当前选中', deviceId)
        this.current.id = deviceId
        this.handleFilesChanged()
      },
      handleFilesChanged (path) {
        if (path) {
          const pathArray = []
          let split = path.split('/')
          split.forEach(function (it, index) {
            pathArray.push({
              name: it,
              path: split.filter(function (s, i) {
                return i <= index
              }).join('/')
            })
          })
          console.info(path)
          this.current.path = path
          this.current.pathArray = pathArray
          console.log(this.current.pathArray)
        }
        console.info('获取文件列表', JSON.stringify(this.current))
        ipcRenderer.send('list-files', this.current)
      },
      handleFilesOptionsChanged (property, value) {
        this.current[property] = value
        this.handleFilesChanged()
      },
      handleUpdateDevices () {
        ipcRenderer.send('list-devices')
      },
      handleClickCopy (e) {
        clipboard.writeText(e)
        this.$message({
          message: '复制成功 - ' + e,
          duration: 1000,
          type: 'success'
        })
      },
      handleClickDownload (item) {
        const items = Array.isArray(item) ? item : [item]
        for (let i = 0; i < items.length; i++) {
          if (items[i].isDirectory) {
            this.$message({
              message: '暂不支持导出文件夹',
              duration: 1000,
              type: 'error'
            })
            return
          }
        }

        const that = this
        const input = document.createElement('input')
        input.type = 'file'
        input.webkitdirectory = true
        input.directory = true
        input.onchange = function () {
          that.handlePullFile(items, input.files[0].path)
        }
        input.click()
      },
      handlePullFile (items, outputDir) {
        ipcRenderer.send('pull-files', {
          id: this.current.id,
          parent: this.current.path,
          items: items,
          outputDir: outputDir
        })
      },
      handleSelectionChange (val) {
        this.selectionFiles = val
      }
    },
    mounted () {
      ipcRenderer.on('list-devices-rsp', (event, data) => {
        console.info(data)
        this.devices = data
        this.handleDeviceChanged(data.length > 0 ? data[0].id : null)
      })

      ipcRenderer.on('list-files-rsp', (event, data) => {
        console.info(data)
        this.files = data
      })

      ipcRenderer.on('pull-file-progress', (event, data) => {
        console.log(data)
        let item = this.files[data.index]
        if (item.path === data.path) {
          this.$set(item, 'progress', data.progress)
        }
        console.info('导出进度', data)
      })
      ipcRenderer.on('pull-file-success', (event, data) => {
        console.info('导出成功', data)
      })
      ipcRenderer.on('pull-file-error', (event, data) => {
        console.info('导出失败', data)
      })

      this.handleUpdateDevices()
    }
  }
</script>

<style lang="scss">
    @import '../assets/font/iconfont.css';

    .container {
        margin: 15px;
    }

    .el-table {
        th {
            padding-top: 3px !important;
            padding-bottom: 3px !important;;
        }

        .cell {
            font-size: 14px;
        }
    }

    .el-select {
        width: 100%;
    }

    .header {
        display: flex;

        .btn-update {
            margin-left: 10px;
        }
    }

    .el-link {
        font-weight: normal !important;
    }

    .separate {
        border-left: 1px solid #DCDFE6;
        margin: 8px 20px;
    }

    .copy-path-link {
        text-align: right;
        font-size: 12px;
    }

    .breadcrumb {
        margin-top: 15px;
        margin-bottom: 15px;
    }

    .breadcrumb-row {
        display: flex;
        justify-content: center;
        align-items: center
    }

    .el-checkbox {
        margin-right: 15px !important;
    }

    .el-checkbox:last-of-type {
        margin-right: 0 !important;
    }

</style>
