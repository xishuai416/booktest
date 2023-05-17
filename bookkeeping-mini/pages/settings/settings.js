import {
  getRequest,
  postRequest
} from '../../utils/request'
// pages/my/my.js
var e = require("../../utils/util.js"),
  time = require('../../utils/util.js');
var app = getApp();
Page({
  // 我的总收入支出统计
  moneyCount: {},
  userInfo: {},
  data: {
    badge: wx.getStorageSync('badge'),
    // 0-image  1-xlsx
    array: [{
      index: 0,
      name: "导出图片"
    }, {
      index: 1,
      name: "导出excel"
    }],
    bookId: wx.getStorageSync('bookId'),
    defaultAvatarUrl: "/images/main/default_avatar.png",
    defaultNickName: "用户名称",
    presentCount: 0,
    practiceday: '',
    studytime: '',
    practicetime: "",
    today_s: "",
    vip: false,
    StatusBar: app.globalData.StatusBar,
    CustomBar: app.globalData.CustomBar,
    ColorList: app.globalData.ColorList,
  },
  onShow() {
    console.log("??????????????")
    var userInfo = wx.getStorageSync("userInfo");
    this.setData({
      userInfo
    })
    // 查询我的待审
    this.queryBadge()

    // 我的收入支出统计
    this.meMoneyCount()
    
  },
  // 刷新badge缓存
  refreshCache() {
    let badge = Number(wx.getStorageSync('badge'))
    this.setData({badge})
    wx.setStorageSync('badge', badge)
    console.log("?????????????????",badge)
    if (badge <= 0) {
      wx.removeTabBarBadge({
        index: 1
      })
    } else {
      wx.setTabBarBadge({
        index: 1,
        text: badge + ''
      })

    }
  },
  meMoneyCount() {
    getRequest("/api/money/my/money/count").then((res) => {
      if (res.status == 0) {
        this.setData({
          moneyCount: res.data
        })
      }
    })
  },
  queryBadge() {
    let that=this
    getRequest("/api/book/user/auditCount").then((res) => {
      if (res.status == 0) {
        wx.setStorage({
          key: "badge",
          data: res.data,
          success: function () {
            that.refreshCache()
          }
        })
      }
    })
  },
  onLoad: function (options) {},
  selectBookStatisticsReport(e) {
    console.log("xxxxxxxx", e)
    this.findBookStatisticsReport({
      reportType: e.detail.value
    })
  },
  // 导出明细
  selectDetailStatisticsReport(e) {
    this.findDetailedStatisticsReport({
      reportType: e.detail.value
    })
  },
  // 登录
  login() {
    wx.navigateTo({
      url: '/component/login/login',
    })
  },
  // 关于我们
  onAbout() {
    wx.navigateTo({
      url: '../about/about',
    })
  },
  onReady: function () {},
  // 导出到用户手机公共方法
  saveImageToPhotosAlbum(res) {
    //wx.saveImageToPhotosAlbum方法：保存图片到系统相册
    wx.saveImageToPhotosAlbum({
      filePath: res.tempFilePath, //图片文件路径
      success: function (data) {
        wx.hideLoading(); //隐藏 loading 提示框
        wx.showModal({
          title: '提示',
          content: '已保存到相册',
          modalType: false,
        })
      },
      // 接口调用失败的回调函数
      fail: function (err) {
        if (err.errMsg === "saveImageToPhotosAlbum:fail:auth denied" || err.errMsg === "saveImageToPhotosAlbum:fail auth deny" || err.errMsg === "saveImageToPhotosAlbum:fail authorize no response") {
          wx.showModal({
            title: '提示',
            content: '需要您授权保存相册',
            modalType: false,
            success: modalSuccess => {
              wx.openSetting({
                success(settingdata) {
                  console.log("settingdata", settingdata)
                  if (settingdata.authSetting['scope.writePhotosAlbum']) {
                    wx.showModal({
                      title: '提示',
                      content: '获取权限成功,再次点击图片即可保存',
                      modalType: false,
                    })
                  } else {
                    wx.showModal({
                      title: '提示',
                      content: '获取权限失败，将无法保存到相册哦~',
                      modalType: false,
                    })
                  }
                },
                fail(failData) {
                  console.log("failData", failData)
                },
                complete(finishData) {
                  console.log("finishData", finishData)
                }
              })
            }
          })
        }
      },
      complete(res) {
        wx.hideLoading(); //隐藏 loading 提示框
      }
    })
  },
  findDetailedStatisticsReport(param) {
    const thiz = this;
    postRequest("/api/money/findDetailedStatisticsReport", {
      bookId: wx.getStorageSync('bookId'),
      ...param
    }).then(res => {
      if (res.status == 0) {
        wx.downloadFile({
          url: res.data[0], // 对象存储文件ID，从上传文件接口或者控制台获取
          success: res => {
            if (param.reportType == 0) {
              thiz.saveImageToPhotosAlbum(res);
            } else {
              const filePath = res.tempFilePath;
              wx.openDocument({
                filePath: filePath,
                showMenu: true,
                success: function (res) {
                  console.log(res, '打开文档成功');
                }
              });
            }

          },
          fail: err => {
            wx.showToast({
              title: "保存失败",
              icon: 'error'
            })
          },
          complete(finishData) {
            wx.hideLoading()
          }
        })
        // wx.previewImage({
        //   current: res.data[0], // 当前显示图片的http链接
        //   urls: res.data // 需要预览的图片http链接列表
        // })
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'error'
        })
      }
    }).catch(err => {
      wx.showToast({
        title: "导出失败",
        icon: 'error'
      })
    })
  },
  findBookStatisticsReport(param) {
    const thiz = this;
    wx.showLoading({
      title: '加载中...'
    });
    postRequest("/api/money/findUserStatisticsReport", {
      bookId: wx.getStorageSync('bookId'),
      type: 0,
      ...param
    }).then(res => {
      if (res.status == 0) {
        wx.downloadFile({
          url: res.data[0], // 对象存储文件ID，从上传文件接口或者控制台获取
          success: res => {
            if (param.reportType == 0) {
              thiz.saveImageToPhotosAlbum(res);
            } else {
              const filePath = res.tempFilePath;
              wx.openDocument({
                filePath: filePath,
                showMenu: true,
                success: function (res) {
                  console.log(res, '打开文档成功');
                }
              });
            }
          },
          fail: err => {
            wx.showToast({
              title: "保存失败",
              icon: 'error'
            })
          },
          complete(finishData) {
            wx.hideLoading()
          }
        })


      } else {
        wx.showToast({
          title: res.msg,
          icon: 'error'
        })
      }
    }).catch(err => {
      wx.showToast({
        title: "导出失败~",
        icon: 'error'
      })
    })
  },

  // 图片放大器
  onPreviewImage(e) {
    let url = e.currentTarget.dataset.url
    console.log("url", url)
    wx.previewImage({
      current: url, // 当前显示图片的http链接
      urls: [url] // 需要预览的图片http链接列表
    })
  },
  openPage: function (a) {
    var e = a.currentTarget.dataset.url;
    wx.navigateTo({
      url: e
    });
  },
  joinVip: function () { //加入VIP
    wx.navigateTo({
      url: '../vip/vip',
    })
  },
  chooseGeren: function () {
    wx.navigateTo({
      url: '../form/form',
    })
  },
  changeView: function () {
    wx.openSetting({
      success: (res) => {
        console.log("授权结果..")
        console.log(res)
        if (!res.authSetting["scope.userInfo"] || !res.authSetting["scope.userLocation"] || !res.authSetting["scope.address"] || !res.authSetting["scope.camera"]) {
          console.log(res)
        }
      }
    })
  },
  sign_in: function () {
    wx.navigateTo({
      url: '../sign_in/sign_in',
    })
  },
  onPullDownRefresh() {

  },
  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function (res) {
    if (res.from === 'button') {
      // 来自页面内转发按钮
      console.log(res.target)
    }
    return {
      title: '看看',
      path: 'pages/study/study',
      success: function (res) {
        // 转发成功
        wx.showShareMenu({
          // 要求小程序返回分享目标信息
          withShareTicket: true
        });
      },
      fail: function (res) {
        // 转发失败
      }
    }
  },
  myAudit() {
    wx.navigateTo({
      url: '../audit/audit',
    })
  },
  myApply() {
    wx.navigateTo({
      url: '../apply/apply',
    })
  },

  clearStorage() {
    wx.removeStorageSync('bookId')
    wx.removeStorageSync('bookName')
    wx.removeStorageSync('badge')
    wx.removeStorageSync('logs')
    wx.removeStorageSync('searchList')
    wx.showToast({
      title: '清除成功',
    })
  },
  logout() {
    wx.removeStorageSync('userInfo')
    wx.removeStorageSync('token')
    this.setData({
      userInfo: null
    })
    wx.showToast({
      title: '成功登出',
    })
  },

  // 触底
  onReachBottom(e) {
    console.log("eeeeeeee触底", e);
  },
  // 邀请好友
  onShareAppMessage: function (ops) {
    if (ops.from === 'button') {
      var title = ops.target.dataset.title;
    };
    return {
      title: `一个超级好用的记账小程序`, //转发的标题。当前小程序名称
      success: function (res) {

      },
      fail: function (res) {
        // 转发失败
      }
    }
  },
})