import {
  formatTime
} from '../../../utils/dateUtil'
import {
  getRequest
} from '../../../utils/request'

Component({
  /**
   * 组件的属性列表
   */
  properties: {
    selectIncome:{

    },
    buttomEmpty:{},
    selectExpenditure:{
      
    },
    noticeMessage: {
      type: Array,
      value: ['未来属于那些相信梦想，并愿意为之付诸行动的人', '全世界都可以是你的，而你只能是我的']
    },
    buttomMessage: {
      type: String,

    },
    exSum: {
      type: Number
    },
    enterSum: {
      type: Number
    },
    bookMoney: {
      type: Array,
      value: []
    },
    bookList: {
      type: Array,
      value: []
    },
    bookId: {},
    bookName: {},

  },
  data: {
    categorySelectList: [],
    bookData: {},
    defaultBook: false,
    moneyId: null,
    // 金额长按触发
    moneyShow: false,
    // 账本弹出变量
    show: false,
    duration: 400,
    position: 'bottom',
    round: true,
    overlay: true,
    customStyle: 'height: 60%;',
    overlayStyle: 'background-color: rgba(0, 0, 0, 0.7)',
    // 默认展示第0个账本
    bookIndex: 0,
    selectData: [{
      id: 0,
      name: '年'
    }, {
      id: 1,
      name: '月'
    }, {
      id: 2,
      name: '日'
    }],
    tabsHeightDesc: 0,
    tabs: [{
      index: -1,
      title: "全部"
    }, {
      index: 0,
      title: "收入"
    }, {
      index: 1,
      title: "支出"
    }],
    timeDesc: '全部',
    query: {
      type: -1
    },
    fields: 'month'
  },
  ready() {
    this.triggerEvent('childrenFindList', this.data.query);

  },
  methods: {
    // 分类过滤
    onMoneyQueryFilterView() {
      console.log("111")
      this.triggerEvent('onMoneyQueryFilterView');
    },
    // // 接受分类传参
    // setCategorySelectList(categorySelectList) {
    //   console.log("接受分类传参，", categorySelectList)
    //   this.setData({
    //     categorySelectList
    //   })
    // },
    bindMultiPickerChange(e) {
      console.log("e", e)
    },
    // 设置默认账本
    defaultBookChange(e) {
      wx.showLoading({
        title: '切换中...',
      })
      console.log(" e.detail.value", e.detail.value)
      var bookId = e.currentTarget.dataset.id
      let defaultBook = e.detail.value == true ? "1" : "0"
      let noDefaultBook = e.detail.value == true ? "0" : "1"
      getRequest("/api/book/defaultBook?bookId=" + bookId + "&defaultBook=" + defaultBook).then(res => {
        if (res.status == 0) {
          for (let i = 0; i < this.data.bookList.length; i++) {
            let key = `bookList[${i}].defaultBook`
            if (this.data.bookList[i].id == bookId) {
              this.setData({
                [key]: 1
              })
            } else if (this.data.bookList[i].defaultBook == 1) {
              this.setData({
                [key]: 0
              })
            }
          }
        } else {
          for (let i = 0; i < this.data.bookList.length; i++) {
            if (this.data.bookList[i].id == bookId) {
              let key = `bookList[${i}].defaultBook`
              this.setData({
                [key]: noDefaultBook
              })
              break;
            }
          }
          wx.showToast({
            title: res.msg,
            icon: 'error'
          })
        }
      }).catch((err) => {
        // 还原为初始值
        this.setData({
          defaultBook: noDefaultBook
        })
        wx.showToast({
          title: err,
          icon: 'error'
        })
      }).finally(() => {
        wx.hideLoading()
      })
      console.log("e", e)
    },
    // 删除金额
    moneyDelete(e) {
      const thiz = this;
      wx.showModal({
        title: '提示',
        content: '删除后不可恢复，确定？',
        success(res) {
          if (res.confirm) {
            // 金额id
            getRequest("/api/money/delete?moneyId=" + thiz.data.moneyId).then(res => {
              if (res.status == 0) {
                thiz.triggerEvent('childrenFindList', thiz.data.query);
                thiz.setData({
                  show: false
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

          } else if (res.cancel) {

          }
        }
      })

    },
    // 取消金额弹出删除
    moneyDeleteClose() {
      this.setData({
        show: false
      })
    },
    longpressMoney(e) {
      this.setData({
        moneyId: e.currentTarget.dataset.moneyid,
        show: true,
        moneyShow: true,
        customStyle: 'height: 20%;'
      })
    },
    addBook() {
      // 新建账本
      wx.navigateTo({
        url: '/pages/index/createBook/bookForm/bookForm'
      })
    },
    // 点击路灯公益触发
    bindBookTapp() {
      this.setData({
        show: true,
        moneyShow: false,
        customStyle: 'height: 60%;'
      })
    },
    // 点击账本设置图标
    onBookSetting(e) {
      // 获取当前账本数据
      var bookData = e.currentTarget.dataset.value;
      console.log("bookData,", bookData)
      wx.navigateTo({
        url: '/pages/book/book',
        success: function (res) {
          // 通过eventChannel向被打开页面传送数据
          res.eventChannel.emit('acceptDataFromOpenerPage', {
            data: bookData,
            invitation: true
          })
        }
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
    // 切换账本触发
    bindBookChange(e) {
      // 账本集合下标
      var index = e.currentTarget.dataset.value;
      var bookId = this.data.bookList[index].id
      var bookName = this.data.bookList[index].bookName
      wx.setStorageSync('bookId', bookId);
      wx.setStorageSync('bookName', bookName);
      // 获取当前选择的账本
      this.triggerEvent('childrenToggleBook', bookId);
      this.setData({
        bookId,
        bookIndex: index,
        bookName,
        show: false
      })
    },
    chooseLs(data) {
      let fields = ''
      let timeQuery = {}
      switch (data.detail.id) {
        case '-1':
          this.setTimeDesc()
          fields = 'all'

          break;
        case 0:
          var y = new Date().getFullYear()
          timeQuery = this.onYearFirstLastDay(new Date())
          this.setTimeDesc(y + '')
          fields = 'year'
          break;
        case 1:
          var dd = new Date();
          var y = dd.getFullYear()
          var m = dd.getMonth() + 1
          timeQuery = this.onMonth(new Date())
          this.setTimeDesc(y + "-" + m)
          fields = 'month'
          break;
        case 2:
          var dd = new Date();
          var y = dd.getFullYear()
          var m = dd.getMonth() + 1
          var d = dd.getDate()
          // 计算面板展示
          var day = y + "-" + m + "-" + d
          this.setTimeDesc(day)
          timeQuery = {
            startQueryTime: day + ' 00:00:00',
            endQueryTime: day + ' 23:59:59'
          }
          fields = 'day'
          break;
        default:
          fields = 'month'
      }
      var query = {
        ...this.data.query,
        ...timeQuery
      }
      if (data.detail.id == "-1") {
        delete query['startQueryTime']
        delete query['endQueryTime']
      }
      this.setData({
        fields,
        query
      })
      this.triggerEvent('setQueryData', query);
    },
    chooseList(data) {
    
      this.chooseLs(data)
      this.triggerEvent('childrenFindList', this.data.query);
    },
    setTimeDesc(value) {
      if (!value) {
        this.setData({
          timeDesc: '全部'
        })
        return;
      }
      let times = value.split("-");
      let timeDesc = ""
      if (times.length == 1) {
        timeDesc = times[0] + '年'
      } else if (times.length == 2) {
        timeDesc = times[1] + '月'
      } else if (times.length == 3) {
        timeDesc = times[1] + '月' + times[2] + '号'
      }
      this.setData({
        timeDesc
      })
    },
    onRefresh() {
      this.chooseLs({
        detail: {
          id: '-1'
        }
      })
      this.triggerEvent('init');
    },
    yearChange(value) {
      this.setTimeDesc(value)
      this.setData({
        query: {
          ...this.data.query,
          ...this.onYearFirstLastDay(new Date(value))
        }
      })
      this.triggerEvent('childrenFindList', this.data.query);
    },
    bindYearChange(e) {
      this.yearChange(e.detail.value)
    },
    bindDayChange(e) {
      this.setTimeDesc(e.detail.value)
      this.setData({
        query: {
          ...this.data.query,
          ...this.onDay(new Date(e.detail.value))
        }
      })
      this.triggerEvent('childrenFindList', this.data.query);
    },
    // 选择月查询
    bindDateChange(e) {
      this.setTimeDesc(e.detail.value)
      this.setData({
        query: {
          ...this.data.query,
          ...this.onMonth(new Date(e.detail.value))
        }
      })

      this.triggerEvent('childrenFindList', this.data.query);
    },
    onTabCLick(e) {
      this.setData({
        query: {
          ...this.data.query,
          type: e.target.id,
          pageNumber: 1

        }
      })
      this.triggerEvent('childrenFindList', this.data.query);
    },
    /**
     * 获取当前年份的第一天和最后一天
     * @returns {string} 例如 2019-01-01~2019-12-31
     */
    onYearFirstLastDay(firstDay) {
      firstDay.setDate(1);
      firstDay.setMonth(0);
      var lastDay = new Date(firstDay);
      lastDay.setFullYear(lastDay.getFullYear() + 1);
      lastDay.setDate(-1);
      firstDay = formatTime(firstDay, "YYYY-MM-dd 00:00:00");
      lastDay = formatTime(lastDay, "YYYY-MM-dd 23:59:59");
      return {
        startQueryTime: firstDay,
        endQueryTime: lastDay
      }
    },
    /**
     * 获取当前年份的第一天和最后一天
     * @returns {string} 例如 2022-05-24 00:00:00 ~ 2022-05-24 23:59:59
     */
    onDay(date) {
      let firstDay = formatTime(date, "YYYY-MM-dd 00:00:00");
      let lastDay = formatTime(date, "YYYY-MM-dd 23:59:59");
      console.log(firstDay)
      console.log(lastDay)
      return {
        startQueryTime: firstDay,
        endQueryTime: lastDay
      }
    },
    /**
     *根据当前时间获取当月第一天和最后一天
     * @param {*} date 
     */
    onMonth(date) {
      //获取当前月的第一天     
      let monthStart = date.setDate(1);
      //获取当前月 
      let currentMonth = date.getMonth();
      //获取到下一个月，++currentMonth表示本月+1，一元运算
      let nextMonth = ++currentMonth;
      //获取到下个月的第一天      
      let nextMonthFirstDay = new Date(date.getFullYear(), nextMonth, 1);
      //一天时间的毫秒数
      let oneDay = 1000 * 60 * 60 * 24;
      //获取当前月第一天和最后一天
      console.log("monthStart", monthStart)
      let firstDay = formatTime(new Date(monthStart), "YYYY-MM-dd 00:00:00");
      //nextMonthFirstDay-oneDay表示下个月的第一天减一天时间的毫秒数就是本月的最后一天
      let lastDay = formatTime(new Date(nextMonthFirstDay - oneDay), "YYYY-MM-dd 23:59:59");
      return {
        startQueryTime: firstDay,
        endQueryTime: lastDay
      }
    },

    // 记一笔的详情
    onMoneyDetail(e) {
      var dataOne = e.currentTarget.dataset.value;
      getRequest('/api/money/my/money/detailDesc?moneyId=' + dataOne.id).then(response => {
        wx.navigateTo({
          url: '/pages/mark/editor/editor',
          success: function (res) {
            // 通过eventChannel向被打开页面传送数据
            res.eventChannel.emit('acceptDataFromOpenerPage', {
              data: response.data,
            })
          }
        })
      })
    },
    // 编辑账本金额
    editMoney(e) {
      // 获取 账本金额选中数据
      var dataOne = e.currentTarget.dataset.value;
      wx.navigateTo({
        url: '/pages/mark/mark?bookId=' + this.data.bookId,
        success: function (res) {
          // 通过eventChannel向被打开页面传送数据
          res.eventChannel.emit('acceptDataFromOpenerPage', {
            dataOne
          })
        }
      })
    },
  },
  // 触底
  onReachBottom(e) {
    console.log("eeeeeeee触底", e);
  }
})