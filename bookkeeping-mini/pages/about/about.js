// pages/about/about.js
import {
  getRequest,
  postRequest
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    buttomMessage: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    // 关于我们
    this.onMessage(3);
  },

  onMessage(type) {
    getRequest("/api/message/get?type=" + type).then(res => {
      if (!res.data) return
      if (res.data.length <= 0) return
      if (res.status == 0) {
        this.setData({
          buttomMessage: res.data[0].message
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },
  copywxtap() {
    wx.setClipboardData({
      data: this.data.buttomMessage,
      success: function (res) {
        wx.getClipboardData({
          success: function (res) {
            console.log(res.data) // data
          }
        })
      }
    })
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