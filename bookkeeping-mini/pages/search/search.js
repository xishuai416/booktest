import * as wxSearch from '../../component/wxSearch/wxSearch';
import {
  getStorage,
  setStorage
} from '../../utils/util';
import {
  getRequest,
  postRequest
} from '../../utils/request'
// pages/search/search.js
Page({
  /**
   * 页面的初始数据
   */
  data: {
    bookList: [],
    tabData: {
      searchList: getStorage('searchList'),
      hotsSearch: ['帽岭村', '卡哇伊', '账本', '账单', '日常', '记账', '工资', '收入', '支出', '公益'],
      activeIndex: 0,
      sliderOffset: 0,
      sliderLeft: 0,
      searchIsHidden: true,
      // 是否关闭热门搜索
      searchAllShow: true,
      inputVal: ''
    }
  },
  onLoad: function (options) {
    //初始渲染-读取storage的历史记录
    wxSearch.init(this)
  },

  // 点击搜索时触发
  bindGoSearch: function (e) {
    console.log("eeeeeeeee", e)
    wxSearch.bindGoSearch(e, this, () => {
      console.log("触发关闭")
      this.setData({
        'tabData.searchIsHidden': true
      })
    })
    this.searchBook(e.currentTarget.dataset.item)
  },


  // 点击搜索图标和确认键盘确认时触发
  bindInputSchool: function (e) {
    console.log("点击搜索图标和确认键盘确认时触发", e)
    wxSearch.bindInputSchool(e, this)
    // this.setData({
    //   'tabData.searchIsHidden': true,
    // })
    // this.searchBook(e.detail.value)
  },
  // 点击热门搜索与历史搜索
  bindGoSchool(e) {
    let val = e.currentTarget.dataset.item;
    console.log("bindGoSchool", val)
    this.setData({
      'tabData.searchIsHidden': true,
      'tabData.inputVal': val
    })
    wxSearch.goSchool(val)
    this.searchBook(val)
  },
  // 收起浏览记录
  putAway(e) {
    wxSearch.putAway(e, this)
  },
  // 点击账本转跳到详情
  onViewBook(e) {
    // 获取当前账本数据
    var bookData = e.currentTarget.dataset.value;
    console.log("bookData,", bookData)


    // 校验是否是账本成员
    getRequest("/api/book/isBookMember?bookId=" + bookData.id).then(res => {
      if (res.status == 0) {
        let invitation = res.data;
        wx.navigateTo({
          url: '/pages/book/book',
          success: function (res) {
            // 通过eventChannel向被打开页面传送数据
            res.eventChannel.emit('acceptDataFromOpenerPage', {
              data: bookData,
              invitation
            })
          }
        })
      } else {
        wx.showToast({
          title: res.msg,
        })
      }
    }).catch(err => {
      wx.showToast({
        title: "网络错误",
      })
    })

  },

  searchBook(bookName) {
    getRequest("/api/book/search", {
      bookName
    }).then(res => {
      if (res.status == 0) {
        this.setData({
          bookList: res.data
        })
      } else {
        wx.showToast({
          title: res.msg,
          success: () => {}
        })
      }
    }).catch(err => {
      wx.showToast({
        title: res,
        success: () => {}
      })
    })
  },






  bindSearchAllShow: function (e) {
    // console.log("bindSearchAllShow", e)
    wxSearch.bindSearchAllShow(e, this)
  },
  bindClearSearch: function () {
    // console.log("bindClearSearch", e)
    wxSearch.updataLog(this, [])
  },
  bindDelLog(e) {
    console.log("bindDelLog", e)
    wxSearch.bindDelLog(e, this)
  },
  bindShowLog(e) {
    var input = e.detail.value;
    console.log("input,", input)
    wxSearch.bindShowLog(e, this)
  },
  bindHideLog(e) {
    console.log("bindHideLog", e)
    wxSearch.bindHideLog(e, this)
  },
  bindSearchHidden() {
    wxSearch.bindSearchHidden(this)
  }
})