import {
  profix,
  getRequest,
  postRequest
} from '../../../utils/request'
Page({
  data: {
    formats: {},
    readOnly: false,
    placeholder: '编辑完成后返回点击"记一笔"即可保存',
    editorHeight: 300,
    keyboardHeight: 0,
    isIOS: false,
    businessId: ''
  },
  readOnlyChange() {
    this.setData({
      readOnly: !this.data.readOnly
    })
  },
  onLoad() {
    const platform = wx.getSystemInfoSync().platform
    const isIOS = platform === 'ios'
    this.setData({
      isIOS
    })
    const that = this
    this.updatePosition(0)
    let keyboardHeight = 0
    wx.onKeyboardHeightChange(res => {
      if (res.height === keyboardHeight) return
      const duration = res.height > 0 ? res.duration * 1000 : 0
      keyboardHeight = res.height
      setTimeout(() => {
        wx.pageScrollTo({
          scrollTop: 0,
          success() {
            that.updatePosition(keyboardHeight)
            that.editorCtx.scrollIntoView()
          }
        })
      }, duration)

    })


    // 处理业务监听，暂
    let thiz = this;
    const eventChannel = this.getOpenerEventChannel()
    // 监听acceptDataFromOpenerPage事件，获取上一页面通过eventChannel传送到当前页面的数据
    eventChannel.on('acceptDataFromOpenerPage', function (response) {
      console.log("response", response)
      wx.createSelectorQuery().select('#editor').context(function (res) {
        console.log("res,res", res)
        thiz.setData({
          businessId: response.businessId
        })
        thiz.editorCtx = res.context
        thiz.editorCtx.setContents({
          html: response.data, //这里就是获取上一页面中的数据
          success: (res) => {
            console.log(res)
          },
          fail: (res) => {
            console.log(res)
          }
        })
      }).exec()
    })
  },
  updatePosition(keyboardHeight) {
    const toolbarHeight = 50
    const {
      windowHeight,
      platform
    } = wx.getSystemInfoSync()
    let editorHeight = keyboardHeight > 0 ? (windowHeight - keyboardHeight - toolbarHeight) : windowHeight
    this.setData({
      editorHeight,
      keyboardHeight
    })
  },
  calNavigationBarAndStatusBar() {
    const systemInfo = wx.getSystemInfoSync()
    const {
      statusBarHeight,
      platform
    } = systemInfo
    const isIOS = platform === 'ios'
    const navigationBarHeight = isIOS ? 44 : 48
    return statusBarHeight + navigationBarHeight
  },
  onEditorReady() {
    const that = this
    wx.createSelectorQuery().select('#editor').context(function (res) {
      console.log("onEditorReady,res", res)
      that.editorCtx = res.context
    }).exec()
  },
  blur() {
    this.editorCtx.blur()
  },
  format(e) {
    let {
      name,
      value
    } = e.target.dataset
    if (!name) return
    // console.log('format', name, value)
    this.editorCtx.format(name, value)

  },

  onStatusChange(e) {
    const formats = e.detail
    this.setData({
      formats
    })
  },
  bindinput(e) {
    console.log("eee", e)
    let pages = getCurrentPages();
    let currPage = null; //当前页面
    let prevPage = null; //上一个页面

    if (pages.length >= 2) {
      currPage = pages[pages.length - 1]; //当前页面
      prevPage = pages[pages.length - 2]; //上一个页面
    }
    if (prevPage) {
      prevPage.setData({
        detailDesc: e.detail.html
      });
    }
    // wx.navigateBack({})
  },

  insertDivider() {
    this.editorCtx.insertDivider({
      success: function () {
        console.log('insert divider success')
      }
    })
  },
  clear() {
    this.editorCtx.clear({
      success: function (res) {
        console.log("clear success")
      }
    })
  },
  removeFormat() {
    this.editorCtx.removeFormat()
  },
  insertDate() {
    const date = new Date()
    const formatDate = `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`
    this.editorCtx.insertText({
      text: formatDate
    })
  },
  insertImage() {
    const that = this



    wx.chooseMedia({
      count: 5,
      success: function (res) {
        Object.keys(res.tempFiles).forEach(index => {
          wx.uploadFile({
            url: `${profix}/api/file/uploads`, //仅为示例，非真实的接口地址
            filePath: res.tempFiles[index].tempFilePath,
            name: 'file',
            formData: {
              'uploadPath': '/money',
              'businessId': that.data.businessId
            },
            header: {
              'Authorization': wx.getStorageSync("token")
            },
            success(res) {
              var response = JSON.parse(res.data);
              console.log("res", response)
              if (response.status == 0) {
                var arrImg = response.data;
                var url = arrImg[0];
                that.editorCtx.insertImage({
                  src: url,
                  data: {
                    id: 'abcd',
                    role: 'god'
                  },
                  width: '80%',
                  success: function () {
                    console.log('insert image success')
                  }
                })
              }
            },
            fail(err) {
              wx.showToast({
                title: err.errMsg,
                icon: 'error'
              })
            },
            complete(err) {
              wx.hideLoading()
            }
          })
        })

      }
    })
  }
})