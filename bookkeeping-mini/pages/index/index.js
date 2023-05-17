// index.js
// 获取应用实例
const app = getApp()

import {
  getRequest,
  postRequest
} from '../../utils/request'
Page({
  data: {
    buttomEmpty: false,
    // 过滤的支出
    selectIncome: 0,
    // 过滤的收入
    selectExpenditure: 0,
    moneyQueryFilterView: false,
    categorySelectList: [],
    noticeMessage: null,
    bookMoneyMap: null,
    triggered: false,
    exSum: 0,
    enterSum: 0,
    tabs: null,
    userInfo: {},
    bookList: [],
    bookMoney: [],
    queryData: {
      pageNumber: 1,
      pageSize: 10,
      categoryIds: []
    },
    bookId: wx.getStorageSync('bookId'),
    bookName: wx.getStorageSync('bookName')
  },
  onShow() {
    this.init();
    // 页面刷新就置空 缓存map
    this.setData({
      bookMoneyMap: null
    })
  },
  queryBadge() {
    getRequest("/api/book/user/auditCount").then((res) => {
      if (res.status == 0) {
        if (res.data != 0) {
          wx.setTabBarBadge({
            index: 1,
            text: res.data + ''
          })
          wx.setStorage({
            key: "badge",
            data: res.data,
            success(res) {
              console.log("Res.", res)
            }
          })

        }
      }
    })
  },
  bindInputSearch() {
    wx.navigateTo({
      url: '/pages/search/search',
    })
  },
  onLoad() {
    // 检测是否已经成功加入邀请的账本
    this.checkShare();
    // 查询我的待审,只进小程序时候执行一次，
    this.queryBadge()
    // 底部技术支持文案
    this.onMessage(2)
    // 通知栏
    this.onMessage(1)
  },

  onMessage(type) {
    getRequest("/api/message/get?type=" + type).then(res => {
      if (res.status == 0) {
        if (type == 1) {
          this.setData({
            noticeMessage: res.data
          })
        } else {
          if (res.data && res.data.length != 0) {
            this.setData({
              buttomMessage: res.data[0].message
            })
          }

        }
      }
    })
  },
  checkShare() {
    wx.getStorage({
      key: 'share',
      success(res) {
        if (res && res.data != null) {
          let options = JSON.parse(res.data)
          // 再次发送请求，加入账本
          getRequest("/api/book/userAddBook?bookId=" + options.bookId + "&remark=来自用户：" + options.userName + "的邀请加入" + "&timeStamp=" + timeStamp).then(res => {
            if (res.status == 0 || res.status == 202) {
              wx.showToast({
                title: res.msg,
                success: () => {
                  // 成功后移除缓存值
                  wx.removeStorageSync('share')
                }
              })
              if (res.status == 202) {
                wx.navigateTo({
                  url: '../apply/apply',
                })
                return;
              }
              if (res.status == 303) {
                wx.showToast(res.msg)
                return;
              }
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
        }
      }
    })
  },
  init() {
    this.setData({
      queryData: {
        pageNumber: 1,
        pageSize: 10,
      },
    })
    getRequest("/api/book/get").then(res => {
      if (null != res.data && res.data.length >= 1) {
        var bookId = res.data[0].id;
        var bookName = res.data[0].bookName;
        // 每次进来都判断是否有bookId
        if (wx.getStorageSync('bookId') == "" || wx.getStorageSync('bookId') == null) {
          wx.setStorageSync('bookId', bookId);
          wx.setStorageSync('bookName', bookName);
        } else {
          // 缓存存在bookId，取缓存的值
          bookId = wx.getStorageSync('bookId')
          // 缓存存在bookName，取缓存的值
          let bool = false;
          bookName = wx.getStorageSync('bookName')

          for (let i = 0; i < res.data.length; i++) {
            if (res.data[i].id == bookId) {
              // 有可能账本被改过名字，但是本地缓存值没更新
              if (res.data[i].bookName != bookName) {
                bookName = res.data[i].bookName
                bool = true;
                wx.setStorageSync('bookName', res.data[i].bookName);
                break;
              }
              break;
            }
            // 如果循环到最后，还是没有找到，则是被删除了
            if (i == res.data.length - 1) {
              bool = true;
            }
          }
          // 如果为true，证明账本被删除了
          if (bool) {
            wx.setStorageSync('bookId', bookId);
            wx.setStorageSync('bookName', bookName);
            bookId = res.data[0].id;
            bookName = res.data[0].bookName;
          }
        }
        this.setData({
          bookId,
          bookName,
          bookList: res.data
        }, () => {
          this.findList(this.data.queryData)
        })
      } else {
        // 已经没有创建或者加入的账本
        wx.removeStorageSync('bookId')
        wx.removeStorageSync('bookName')
        this.setData({
          bookId: null,
          bookName: null,
          bookList: []
        })
      }
    }).finally(() => {
      if (this._freshing)
        this._freshing = false
    })
  },
  // 由home.js调用。控制过滤界面是否打开
  onMoneyQueryFilterView() {
    console.log("this.data.queryData.categoryIds.", this.data.queryData.categoryIds)
    this.data.categorySelectList.forEach(x => {
      if (this.data.queryData.categoryIds)
        if (this.data.queryData.categoryIds && this.data.queryData.categoryIds.indexOf(String(x.categoryId)) != '-1') {
          x['checked'] = true
        }
    })
    this.setData({
      categorySelectList: this.data.categorySelectList,
      moneyQueryFilterView: !this.data.moneyQueryFilterView
    })
  },
  bindclose() {
    this.setData({
      moneyQueryFilterView: false
    })
  },
  setQueryData(e) {
    console.log("eeeeeeee", e)
    this.setData({
      queryData: e.detail
    })
  },
  childrenFindList(e) {
    this.setQueryData(e)
    this.findList(this.data.queryData)
  },
  // 点击账本金额过滤分类确认按钮触发
  bindbuttontap(e) {
    this.setData({
      'queryData.pageNumber': 1
    })
    this.findList(this.data.queryData)
    this.bindclose()
  },
  // 勾选账本金额过滤分类后触发
  checkboxChange(e) {
    this.setData({
      'queryData.categoryIds': e.detail.value
    })
  },
  // 切换账本
  childrenToggleBook(e) {
    this.setData({
      bookMoneyMap: null,
      bookId: e.detail
    })
    this.findList(this.data.queryData)
  },
  tapMark() {
    wx.navigateTo({
      url: '/pages/mark/mark?bookId=' + this.data.bookId
    })
  },
  regFenToYuan(fen) {
    var num = fen;
    num = fen * 0.01;
    num += '';
    var reg = num.indexOf('.') > -1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;
    num = num.replace(reg, '$1');
    num = this.toDecimal2(num)
    return num
  },
  toDecimal2(x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
      return false;
    }
    var f = Math.round(x * 100) / 100;
    var s = f.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
      rs = s.length;
      s += '.';
    }
    while (s.length <= rs + 2) {
      s += '0';
    }
    return s;
  },

  findList(data) {
    postRequest("/api/money/findList", {
      bookId: this.data.bookId,
      ...data
    }).then(res => {
      if (res.status == 0) {
        // 根据时间分组
        var exSum = 0;
        var enterSum = 0;
        enterSum = this.regFenToYuan(res.data.enterSum);
        exSum = this.regFenToYuan(res.data.exSum);
        this.setData({
          bookMoneyMap: null,
          exSum,
          enterSum,
          selectIncome: this.regFenToYuan(res.data.selectIncome),
          selectExpenditure: this.regFenToYuan(res.data.selectExpenditure),
        })
        this.buildData(res.data.bookMoneyList)
        this.setData({
          categorySelectList: res.data.categorySelectList
        })
        // 调用home.js的setCategorySelectList函数，将下拉数据categorySelectList传递过去
        // this.selectComponent("#homeBook").setCategorySelectList(categorySelectList)
      } else {
        wx.showToast({
          title: res.msg,
          icon: "error"
        })
      }
    }).catch(err => {
      wx.showToast({
        title: '网络异常，请下拉尝试',
        icon: "none"
      })
    }).finally(() => {
      this.setData({
        triggered: false
      })
      this._freshing = false
      wx.stopPullDownRefresh()
    })
  },
  buildData(bookMoneyList) {
    // 根据时间分组
    let bookMoney = [];
    let map = this.data.bookMoneyMap;
    if (map == null) {
      map = new Map()
    }
    // // 过滤的支出
    // let selectIncome = 0
    // // 过滤的收入
    // let selectExpenditure = 0
    bookMoneyList.list.forEach(x => {
      // if (x.type == 1 && x.money != null) {
      //   selectIncome += Number(x.money)
      // }
      // if (x.type == 0 && x.money != null) {
      //   selectExpenditure += Number(x.money)
      // }

      x.money = this.regFenToYuan(x.money)
      if (!map.get(x.bookTimeDesc)) map.set(x.bookTimeDesc, [])
      let groupArr = map.get(x.bookTimeDesc);
      groupArr.push(x);
      map.set(x.bookTimeDesc, groupArr)
    })
    for (var [key, value] of map) {
      let bookList = value.sort(function (obj1, obj2) {
        if (obj1.createTime > obj2.createTime) return 1;
        else if (obj1.createTime > obj2.createTime) return -1;
        else return 0;
      })
      var obj = {};
      bookList = bookList.reduce(function (item, next) {
        obj[next.id] ? '' : obj[next.id] = true && item.push(next);
        return item;
      }, []);

      let expenditure = 0;
      bookList.filter(x => x.type == 0 && x.money != null).forEach(data => expenditure += Number(data.money))
      let income = 0;
      bookList.filter(x => x.type == 1 && x.money != null).forEach(data => income += Number(data.money))
      bookMoney.push({
        groupByDesc: `今日支出 ${income?this.moneyFormat(income):0}  今日收入 ${expenditure?this.moneyFormat(expenditure):0}`,
        bookTimeDesc: key,
        bookList
      })
    }

    let bookMoneySort = bookMoney.sort(function (obj1, obj2) {
      let o1 = new Date(obj1.bookTimeDesc).getTime()
      let o2 = new Date(obj2.bookTimeDesc).getTime()
      if (o1 < o2) return 1;
      else if (o1 > o2) return -1;
      else return 0;
    })
    console.log("bookMoneySort", bookMoneySort)
    // this.regFenToYuan(exSum)
    this.setData({
      bookMoneyMap: map,
      bookMoney,
      'queryData.totalCount': bookMoneyList.totalCount,
      'queryData.totalPage': bookMoneyList.totalPage,
      'queryData.pageNumber': bookMoneyList.pageNumber,
      'queryData.pageSize': bookMoneyList.pageSize,
    })
  },
  moneyFormat(money) {
    console.log("money", money)
    if (String(money).length >= 7) {
      // {value: '152.87', unit: '万'}
      let moneyObj = this.numberFormat(money);
      return moneyObj.value + moneyObj.unit
    }
    return money;
  },
  /**
   * 超过万就展示单位
   * 
   * @param {*} value 
   * return {value: '1521.45', unit: '万'}
   */
  numberFormat(value) {
    var param = {};
    var k = 10000,
      sizes = ['', '万', '亿', '万亿'],
      i;
    if (value < k) {
      param.value = value.toFixed(2)
      param.unit = ''
    } else {
      i = Math.floor(Math.log(value) / Math.log(k));
      param.value = ((value / Math.pow(k, i))).toFixed(2);
      param.unit = sizes[i];
    }
    return param;
  },

  // 小程序自带下拉刷新方式
  onPullDownRefresh() {
    if (this._freshing) return
    this._freshing = true
    this.selectComponent("#homeBook").onRefresh()
    // this.init()
  },

  // 使用scroll-view方式
  // onRefresh() {
  //   if (this._freshing) return
  //   this._freshing = true
  //   this.selectComponent("#homeBook").onRefresh()
  // },

  onRestore(e) {
    console.log('onRestore:', e)
  },

  onAbort(e) {
    console.log('onAbort', e)
  },


  // 小程序自带触底
  onReachBottom(e) {
    console.log("this.data.queryData", this.data.queryData)
    if (this.data.queryData.pageNumber * this.data.queryData.pageSize >= this.data.queryData.totalCount) {
      this.setData({
        buttomEmpty: true
      })
      // wx.showToast({
      //   title: '到底啦',
      // })
      return;
    }
    this.findPage({
      ...this.data.queryData,
      pageNumber: this.data.queryData.pageNumber + 1
    })
  },


  findPage(data) {
    postRequest("/api/money/findPage", {
      bookId: this.data.bookId,
      ...data
    }).then(res => {
      // 根据时间分组
      if (res.status == 0) {
        // this.setData({
        //   selectIncome: this.regFenToYuan(res.data.selectIncome),
        //   selectExpenditure: this.regFenToYuan(res.data.selectExpenditure),
        // })
        this.buildData(res.data)
      } else {
        wx.showToast({
          title: '网络异常',
        })
      }
    }).catch(err => {
      wx.showToast({
        title: '网络异常，请尝试',
        icon: "error"
      })
    }).finally(() => {

    })
  },
  onShareAppMessage() {
    return {
      title: '蜻蜓记账本，随心所记',
      path: '/pages/index/index',
    }
  },
  onShareTimeline: function (ops) {
    return {
      title: '蜻蜓记账本，随心所记',
      path: '/pages/index/index',
    }
  }
})