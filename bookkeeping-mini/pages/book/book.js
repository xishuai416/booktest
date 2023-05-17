import {
  profix,
  getRequest,
  postRequest
} from '../../utils/request'
Page({
  onShareAppMessage: function (ops) {
    if (ops.from === 'button') {
      var title = ops.target.dataset.title;
    };
    return {
      title: `${wx.getStorageSync('userInfo').nickName}邀请你加入${this.data.bookName}账本`, //转发的标题。当前小程序名称
      path: `/pages/share/share?bookId=` + this.data.bookId + "&userName=" + wx.getStorageSync('userInfo').nickName + "&bookName=" + this.data.bookName + "&timeStamp=" + new Date().getTime(), //转发的路径
      imageUrl: '', //自定义图片路径 支持PNG及JPG。显示图片长宽比是 5:4。
      success: function (res) {

      },
      fail: function (res) {
        // 转发失败
      }
    }
  },

  onShareTimeline(ops) {
    return {
      title: `${wx.getStorageSync('userInfo').nickName}邀请你加入${this.data.bookName}账本`, //转发的标题。当前小程序名称
      // query: {
      //   shareTimeline: "1",
      //   invitation: JSON.stringify(this.data.invitation),
      //   bookData: JSON.stringify(this.data.bookData),
      //   bookId: this.data.bookId
      // },
      query: "shareTimeline=1" + "&invitation=" + JSON.stringify(this.data.invitation) + "&bookData=" + JSON.stringify(this.data.bookData) + "&bookId=" + this.data.bookId,
      //  `bookId=` + this.data.bookId + "&userName=" + wx.getStorageSync('userInfo').nickName + "&bookName=" + this.data.bookName+"&timeStamp="+new Date().getTime(), //转发的路径
      imageUrl: '', //自定义图片路径 支持PNG及JPG。显示图片长宽比是 5:4。
      success: function (res) {},
      fail: function (res) {
        // 转发失败
      }
    }
  },
  /**
   * 页面的初始数据
   */
  data: {
    invitation: true,
    now: wx.getStorageSync('userInfo'),
    // 被选中要修改的数据
    userData: {},
    // 被选中要修改的用户名称
    userName: null,
    bookUserModalshow: false,
    // 被长按的用户权限
    auth: null,
    // 被长按的用户id
    bookUserId: null,
    // 当前创建人id
    // userId: wx.getStorageSync('userInfo').id,
    bookData: null,
    bookId: null,
    bookName: '',
    bookAvatar: '',
    // 底部删除用户弹出变量
    show: false,
    duration: 400,
    position: 'bottom',
    round: true,
    overlay: true,
    overlayStyle: 'background-color: rgba(0, 0, 0, 0.7)',
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(option) {

    let thiz = this;
    const eventChannel = this.getOpenerEventChannel()
    console.log("option:", option)
    if (option.shareTimeline == "1") {
      let res = option
      thiz.setData({
        invitation: JSON.parse(res.invitation),
        bookData: JSON.parse(res.bookData),
        bookId: res.bookId
      })
      thiz.bookUser({
        bookId: res.bookId
      });
    } else {
      // 监听acceptDataFromOpenerPage事件，获取上一页面通过eventChannel传送到当前页面的数据
      eventChannel.on('acceptDataFromOpenerPage', function (res) {
        thiz.setData({
          invitation: res.invitation,
          bookData: res.data,
          bookId: res.data.id
        })
        thiz.bookUser({
          bookId: res.data.id
        });
      })

    }

  },
  // 成员加入需管理员审核开关触发
  userAuditChange(e) {
    let flag = e.detail.value

    this.setData({
      'bookData.userAudit': flag == false ? 0 : 1
    })
  },

  // 转跳到申请加入账本界面
  bookSend() {
    wx.navigateTo({
      url: '/pages/book/send/book-send?bookId=' + this.data.bookId +
        '&bookAvatar=' + this.data.bookAvatar +
        '&bookName=' + this.data.bookName
    })

  },
  // 私密账本开关触发
  showSearchChange(e) {
    let flag = e.detail.value
    this.setData({
      'bookData.showSearch': flag == true ? 0 : 1
    })

    flag == true ? 0 : 1
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
  submit(e) {
    if (e.detail == 'user') {
      postRequest("/api/book/user/update", {
        bookId: this.data.bookId,
        userName: this.data.userName,
        avatarUrl: this.data.userData.avatarUrl,
        userId: this.data.userData.userId
      }).then(res => {
        if (res.status == 0) {
          this.bookUser({
            bookId: this.data.bookId
          });
          //  关闭弹窗
          this.selectComponent("#userDrawer").close()
        } else {
          wx.showToast({
            title: res.msg,
            icon: 'error'
          })
        }
      }).catch(err => {
        wx.showToast({
          title: err,
          icon: 'error'
        })
      })

    } else if (e.detail == 'book') {
      postRequest("/api/book/saveAndUpdate", {
        ...this.data.bookData,
        id: this.data.bookId,
        bookName: this.data.bookName,
        bookAvatar: this.data.bookAvatar
      }).then(res => {
        if (res.status == 0) {
          //  关闭弹窗
          this.selectComponent("#bookDrawer").close()
        } else {
          wx.showToast({
            title: res.msg,
            icon: 'error'
          })
        }
      }).catch(err => {
        wx.showToast({
          title: err,
          icon: 'error'
        })
      })
    } else {
      wx.showToast({
        title: "UI提交异常",
        icon: 'error'
      })
    }
  },
  uploads(e) {
    // 全局遮罩层

    const thiz = this;
    wx.chooseImage({
      success(res) {
        wx.showLoading({
          title: '上传中...',
          mask: true
        })
        const tempFilePaths = res.tempFilePaths
        wx.uploadFile({
          url: `${profix}/api/file/uploads`, //仅为示例，非真实的接口地址
          filePath: tempFilePaths[0],
          name: 'file',
          formData: {
            'uploadPath': '/avatar',
            'businessId': thiz.data.bookId,
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
              if (e.currentTarget.dataset.identifying == 'user') {
                thiz.setData({
                  'userData.avatarUrl': url
                })
              } else {
                thiz.setData({

                  bookAvatar: url
                })
              }
            }
            //do something
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
      }
    })
  },
  containerClose() {
    this.setData({
      show: false,
    })
  },
  // 设置管理员
  setUserAdmin() {
    const thiz = this;
    let bookId = this.data.bookId;
    getRequest("/api/book/user/addAdmin?bookId=" + bookId + "&userId=" + thiz.data.bookUserId).then(res => {
      if (res.status == 0) {
        this.setData({
          show: false,
        })
        thiz.bookUser({
          bookId
        });
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'error'
        })
      }
    }).catch((err) => {
      wx.showToast({
        title: err,
        icon: 'error'
      })
    })
  },
  // 用户主动退出账本
  signOutBook() {
    const thiz = this;
    let bookId = this.data.bookId;
    wx.showModal({
      title: '提示',
      content: '确定要退出账本？',
      success(res) {
        if (res.confirm) {
          getRequest("/api/book/user/signOutBook?bookId=" + bookId).then(res => {
            if (res.status == 0) {
              // 用户退出账本需要移除缓存，不然主页展示的还是退出账本的数据
              wx.removeStorage({
                key: 'bookId',
                success() {
                  wx.removeStorageSync('bookName')
                  wx.switchTab({
                    url: '/pages/index/index'
                  })
                }
              })
            } else {
              wx.showToast({
                title: res.msg,
                icon: 'error'
              })
            }
          }).catch((err) => {
            wx.showToast({
              title: err,
              icon: 'error'
            })
          })
        } else if (res.cancel) {}
      }
    })


  },
  // 移除管理员
  deleteUserAdmin() {
    const thiz = this;
    let bookId = this.data.bookId;
    getRequest("/api/book/user/removeAdmin?bookId=" + bookId + "&userId=" + thiz.data.bookUserId).then(res => {
      if (res.status == 0) {
        this.setData({
          show: false,
        })
        thiz.bookUser({
          bookId
        });
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'error'
        })
      }
    }).catch((err) => {
      wx.showToast({
        title: err,
        icon: 'error'
      })
    })
  },
  // 解散账本
  deleteBook() {
    const thiz = this;
    let bookId = this.data.bookId;
    wx.showModal({
      title: '提示',
      content: '确定要解散账本？',
      success(res) {
        if (res.confirm) {
          getRequest("/api/book/delete?bookId=" + bookId).then(res => {
            if (res.status == 0) {
              wx.switchTab({
                url: '/pages/index/index'
              })
            } else {
              wx.showToast({
                title: res.msg,
                icon: 'error'
              })
            }
          }).catch((err) => {
            wx.showToast({
              title: err,
              icon: 'error'
            })
          })

        } else if (res.cancel) {}
      }
    })
  },
  // 长按绑定
  longpressBookUser(e) {
    this.setData({
      bookUserId: e.currentTarget.dataset.userid,
      auth: e.currentTarget.dataset.auth,
      show: true,
      moneyShow: true
    })
  },
  bookUserDelete() {
    const thiz = this;
    let bookId = this.data.bookId;
    wx.showModal({
      title: '提示',
      content: '你确定要移除该成员？',
      success(res) {
        if (res.confirm) {
          getRequest("/api/book/user/remove?bookId=" + bookId + "&userId=" + thiz.data.bookUserId).then(res => {
            if (res.status == 0) {
              thiz.setData({
                show: false,
              })
              thiz.bookUser({
                bookId
              });
            } else {
              wx.showToast({
                title: res.msg,
                icon: 'error'
              })
            }
          }).catch((err) => {
            wx.showToast({
              title: err,
              icon: 'error'
            })
          })

        } else if (res.cancel) {}
      }
    })

  },

  // 点击用户范围打开弹窗
  onUserView(e) {
    // data-data="{{item}}"  data-userid="{{item.userId}}" data-auth="{{item.auth}}"
    // 获取当前选中的bookUser下的userId，这个userId是book_user下的user_id字段，指向book_user表的id
    this.setData({
      userName: e.currentTarget.dataset.data.userName,
      userData: e.currentTarget.dataset.data,
    })
    console.log(" e.currentTarget.dataset.data", e.currentTarget.dataset.data)
    this.selectComponent("#userDrawer").open()
  },
  // 点击账本范围
  onBookView() {
    this.selectComponent("#bookDrawer").open()
  },

  bookUser(queryData) {
    getRequest("/api/book/user/get", queryData).then(res => {
      // 构造用户列表hash表
      this.setData({
        bookId: res.data.id,
        bookName: res.data.bookName,
        bookAvatar: res.data.bookAvatar,
        bookUser: res.data.bookUserList
      })
    })
  },

})