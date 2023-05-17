import {
  getRequest,
  postRequest
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    options: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      options
    })
    var thiz = this;
    wx.showModal({
      title: '提示',
      content: `是否加入${options.userName}分享的${options.bookName}账本？`,
      success(res) {
        // 加入前先往缓存设置值，加入成功后移除缓存值
        wx.setStorage({
          key: "share",
          data: JSON.stringify({
            bookId: options.bookId,
            userName: options.userName
          }),
          success() {
            thiz.add(options.bookId, "来自用户：" + options.userName + "的邀请加入", options.timeStamp)
          }
        })
      },
      fail(res) {
        wx.switchTab({
          url: '/pages/index/index',
        })
      }
    })
  },

  add(bookId, remark, timeStamp) {
    getRequest("/api/book/userAddBook?bookId=" + bookId + "&remark=" + remark + "&timeStamp=" + timeStamp).then(res => {
      if (res.status == 202 || res.status == 0) {
        // 后端返回 202 ，即为账本需要管理员审核
        wx.removeStorage({
          key: 'share',
          success(resStorage) {
            if (res.status == 0) {
              wx.switchTab({
                url: '/pages/index/index',
              })
            } else {
              wx.navigateTo({
                url: '../apply/apply',
              })
            }
          }
        })
      } else {
        wx.showToast({
          title: res.msg,
          success: () => {
            wx.switchTab({
              url: '/pages/index/index',
            })
          }
        })
      }
    }).catch(err => {

      wx.showToast({
        title: "加入账本失败！",
        success: () => {
          wx.switchTab({
            url: '/pages/index/index',
          })
        }
      })
    })
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})