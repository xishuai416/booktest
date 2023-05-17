import {
  getRequest,
  postRequest
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

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
    this.auditAll()
  },
  // 拒绝
  bindReject(e) {
    let item = e.currentTarget.dataset.value
    console.log(item);
    postRequest("/api/book/user/reject", {
      bookId: item.bookId,
      userId: item.userId
    }).then(res => {
      if (res.status == 0) {
        this.auditAll()
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'error'
        })
      }
    })
  },
  // 同意
  bindAgree(e) {
    let item = e.currentTarget.dataset.value
    console.log(item);
    postRequest("/api/book/user/agree", {
      bookId: item.bookId,
      userId: item.userId
    }).then(res => {
      if (res.status == 0) {
        this.auditAll()
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'error'
        })
      }
    })
  },
  // 我的待审核
  auditAll() {
    getRequest("/api/book/user/auditAll").then((res) => {
      if (res.status == 0) {
        this.setData({
          userList: res.data
        })
      }
    })
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