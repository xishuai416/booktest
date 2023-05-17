// index.js
// 获取应用实例
const app = getApp();
let time = null;
var ctx;
import {
  postRequest
} from '../../../../utils/request'
Page({
  data: {
    index: null
  },
  formSubmit: function (e) {
    let bookName = e.detail.value.bookName;
    let defaultBook = e.detail.value.defaultBook == true ? '1' : '0';
    let userAudit = e.detail.value.userAudit == true ? '1' : '0';
    let showSearch = e.detail.value.showSearch == true ? '0' : '1';
    if (bookName == '' || bookName == null) {
      wx.showToast({
        title: '账本名称不能为空',
        icon: 'none'
      })
      return;
    }
    postRequest("/api/book/saveAndUpdate", {
      bookName,
      defaultBook,
      showSearch,
      userAudit
    }).then(res => {
      wx.showToast({
        title: '账本创建成功',
        icon: 'none'
      })
      wx.switchTab({
        url: '/pages/index/index'
      })
    })
  },

})