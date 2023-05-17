
import {profix} from "../../utils/request"
const app = getApp()
// component/login/login.js
Page({

  /**
   * 页面的初始数据
   */
  data: {

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },
  close() {
    wx.switchTab({
      url: '/pages/index/index'
    })
  },
  getUserProfile() {
    wx.getUserProfile({
      desc: '用于完善会员资料', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
      success: (resget) => {
        wx.login({
          success(reslogin) {
            if (reslogin.errMsg == "login:ok") {
              //发起网络请求
              wx.request({
                url: `${profix}/api/user/login`,
                method: "post",
                data: {
                  code: reslogin.code,
                  rawData: resget.rawData,
                  signature: resget.signature,
                  iv: resget.iv,
                  encryptedData: resget.encryptedData
                },
                success: (res) => {
                  let rest = res.data;
                  if (res.data.status == 0) {
                    wx.hidenM
                    // 存储用户信息
                    wx.setStorage({
                      key: 'userInfo',
                      data: rest.data
                    })
                    // 存储token
                    wx.setStorage({
                      key: 'token',
                      data: rest.data.token
                    })
                    
                    wx.switchTab({
                      url: '/pages/index/index',
                    })
                  } else {
                    wx.showToast({
                      title: '服务器 ' + res.data.status + ' 错误',
                      icon: 'none'
                    });
                  }
                }
              })
            } else {
              console.log('登录失败！' + res.errMsg)
            }
          }
        })
      }
    })
  },
  bindgetuserinfo(res) {
    console.log("res", res)
    if (res.detail.errMsg == 'getUserInfo:ok') {
      let userInfo = {
        ...res.detail.userInfo
      }
      wx.login({
        success: e => {
          let code = e.code; //调用wx.login，获取登录凭证（code），并调用接口，将code发送到第三方客户端
          // server.sendRequest({
          //   url: '', //小程序端将code传给第三方服务器端，第三方服务器端调用接口，用code换取session_key和openid
          //   data: {
          //     encryptedData: res.detail.encryptedData,
          //     iv: res.detail.iv,
          //     code: code
          //   },
          //   method: 'POST',
          //   success: res => {
          //     if (res.data.code == 200) {
          //       userInfo = {
          //         ...userInfo,
          //         ...res.data.result
          //       }
          //       console.log(userInfo);
          //       console.log(res.data.result)
          //       wx.setStorageSync('userInfo', userInfo);
          //       //授权成功
          //       this.triggerEvent('login', {
          //         status: 1
          //       })
          //       this.$Message({
          //         content: '登录成功',
          //         type: "success"
          //       })
          //       this.handleHide();
          //     } else {
          //       this.triggerEvent('login', {
          //         status: 0
          //       })
          //       this.$Message({
          //         content: '登录失败',
          //         type: 'error'
          //       });
          //       this.handleHide();
          //     }
          //   }
          // })
        }
      })
    } else {
      this.triggerEvent('login', {
        status: 0
      })
      this.$Message({
        content: '登录失败',
        type: 'error'
      });
      this.handleHide();
    }
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