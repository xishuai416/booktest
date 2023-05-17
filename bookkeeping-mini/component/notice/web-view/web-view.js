const app = getApp()
Page({
  data: {
    url: '',
  },
//decodeURIComponent()函数可以识别并且分立出url
  onLoad: function (options) {
    console.log('options',options);
    let fromPath=decodeURIComponent(options.url);
    this.setData({
      url:fromPath
    })
  }
})