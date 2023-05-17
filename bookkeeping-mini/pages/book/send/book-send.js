import {
  postRequest
} from '../../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    bookInfo: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      bookInfo: options
    })

  },
  // 提交加入账本申请
  formSubmit(e) {
    // if (!e.detail.value.bookName) {
    //   wx.showToast({
    //     title: '请填写备注',
    //   })
    // }
    postRequest("/api/book/user/send/apply", {
      bookId: this.data.bookInfo.bookId,
      remark: e.detail.value.bookName,
    }).then(res => {
      if (res.status == 0) {
        wx.showToast({
          title: '加入成功',
          success: function () {
            setTimeout(() => {
              wx.navigateBack({
                delta: 1
              })
            }, 1500)
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
        title: err,
        icon: 'error'
      })
    })

    console.log("Eeeeee", e)
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})