let c = require('../../utils/common.js');
import {
  postRequest,
  getRequest
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    detailDesc: '',
    initData: {},
    userData: [],
    initUserInfo: {
      initUserId: null,
      initUserName: null,
    },
    initUserId: null,
    initUserName: null,
    bookId: '',
    initDate: '',
    remark: '',
    money: '',
    zcCategoryList: [],
    srCategoryList: [],
    zcCategorySelectedIndex: 0,
    srCategorySelectedIndex: 0,
    // 支出-1 收入-0
    tabActive: 1,
    id: null,
    categoryIconSr: false,
    categoryIconZc: false
  },


  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log("options:", options)
    var thiz = this;
    let param = null
    const eventChannel = this.getOpenerEventChannel()
    eventChannel.on('acceptDataFromOpenerPage', function (data) {
      // 编辑时初始化参数
      param = data.dataOne
      console.log(param, "111")
      thiz.setData({
        tabActive: param.type,
        money: param.money != null && param.money != '' ? param.money : thiz.data.money,
        initData: param,
        initUserInfo: {
          userData: thiz.data.userData,
          initUserId: param.userId,
          initUserName: param.userName
        }
      })
      // 查询富文本内容
      getRequest('/api/money/my/money/detailDesc?moneyId=' + param.id).then(res => {
        if (!thiz.data.detailDesc) {
          thiz.setData({
            detailDesc: res.data
          })
        }
      })
    })
    thiz.init(options)
  },
  // 初始化工作，查询分类等
  init(options) {
    console.log("执行初始化：", options)
    this.setData({
      bookId: options.bookId
    })
    let id = options.id;
    if (id != undefined && id != null) {
      this.data.id = id;
    }
    this.getCategory();
    this.bookUser();
  },
  numChange: function (e) {
    this.setData({
      money: e.detail.money
    })
  },
  bookUser: function () {
    getRequest("/api/book/user/get", {
      bookId: this.data.bookId
    }).then(res => {
      // 构造用户列表hash表
      res.data.bookUserList.forEach(user => {
        user.userName = user.userName != '' && user.userName != null ? user.userName : user.reallyName;
      })
      this.setData({
        userData: [{
          id: -1,
          userName: '自定义'
        }, ...res.data.bookUserList]
      })
    })
  },
  submit: function (e) {
    if (!c.checkStrNotNull(this.data.money)) {
      wx.showToast({
        title: "请输入金额",
        icon: 'error'
      });
      return;
    }

    if (e.detail.remark != null && e.detail.remark.length > 25) {
      wx.showToast({
        title: "备注超过25个字",
        icon: 'error'
      });
      return;
    }
    let categoryItem = this.data.tabActive == 1 ?
      this.data.zcCategoryList[this.data.zcCategorySelectedIndex] :
      this.data.srCategoryList[this.data.srCategorySelectedIndex];
    // 微信组件editor每次编辑详情之后，清空里面的值，就会留下字符串"<p><br></p>" 所以判断如果等于“<p><br></p>”，直接清空为空串
    let detailDesc = this.data.detailDesc == "<p><br></p>" ? "" : this.data.detailDesc
    let params = {
      ...e.detail,
      money: parseFloat(e.detail.money) * 100,
      type: this.data.tabActive,
      bookId: this.data.bookId, //账单id
      bookTime: e.detail.date,
      categoryId: categoryItem.id,
      categoryName: categoryItem.name,
      remark: e.detail.remark,
      detailDesc,
    }
    var flag = true;
    let url = '/api/money/save';
    if (params.id != null) {
      url = '/api/money/update'
      flag = false;

    }
    postRequest(url, params).then(res => {
      if (res.status == 0) {
        this.setData({
          detailDesc: ""
        })
        var title = flag ? '成功记一笔' : '修改成功'
        wx.showToast({
          title,
          icon: 'success'
        })
        // 如果是修改，回到主页
        if (!flag) wx.switchTab({
          url: '/pages/index/index'
        })
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'error'
        })
      }

    })
  },
  //获取分类数据
  getCategory() {
    let thiz = this;
    getRequest('/api/category/findBookIdList?bookId=' + this.data.bookId).then(res => {
      thiz.setData({
        srCategoryList: res.data.srCategory,
        zcCategoryList: res.data.zcCategory
      })
      // 寻找分类
      thiz.setCatIndex();
    })
  },
  setCatIndex() {
    //  默认收入支出类型
    var tabActive = this.data.tabActive
    var categoryKey = 'srCategorySelectedIndex'
    var categoryIndex = 0
    //默认分类
    if (tabActive == 1) {
      //  分类类型（0-收入 1-支出）
      categoryKey = 'zcCategorySelectedIndex'
      for (let i = 0; i < this.data.zcCategoryList.length; i++) {
        if (this.data.zcCategoryList[i].id == this.data.initData.categoryId) {
          categoryIndex = i
          break;
        }
      }
    } else {
      for (let i = 0; i < this.data.srCategoryList.length; i++) {
        if (this.data.srCategoryList[i].id == this.data.initData.categoryId) {
          categoryIndex = i
          break;
        }
      }
    }
    var data = {}
    data[categoryKey] = categoryIndex
    this.setData(data)
  },
  onViewCategoryIcon(e) {
    this.setData({
      categoryIconZc: false,
      categoryIconSr: false,
    })
  },
  onViewCategoryIconZc(e) {
    this.setData({
      categoryIconZc: !this.data.categoryIconZc
    })
  },
  onViewCategoryIconSr(e) {
    this.setData({
      categoryIconSr: !this.data.categoryIconSr
    })
  },
  onRemoveCategory(e) {
    // 分类id
    let categoryId = e.currentTarget.dataset.id;
    getRequest("/api/category/remove", {
      categoryId
    }).then(res => {
      if (res.status == 0) {
        // 支出-1 收入-0
        if (this.data.tabActive == 1) {
          for (let i = this.data.zcCategoryList.length - 1; i >= 0; i--) {
            // 前端做支出分类逻辑删除
            if (this.data.zcCategoryList[i].id == categoryId) {
              this.data.zcCategoryList.splice(i, 1)
              this.setData({
                zcCategoryList: this.data.zcCategoryList
              })
              return;
            }
          }
        } else if (this.data.tabActive == 0) {
          for (let i = this.data.srCategoryList.length - 1; i >= 0; i--) {
            // 前端做收入分类逻辑删除
            if (this.data.srCategoryList[i].id == categoryId) {
              this.data.srCategoryList.splice(i, 1)
              this.setData({
                srCategoryList: this.data.srCategoryList
              })
              return;
            }
          }
        }
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'none'
        });
      }
    }).catch(err => {
      wx.showToast({
        title: "删除失败",
        icon: 'error'
      });
    })
  },
  //分类被选中
  categroyItemSelect(e) {
    let index = e.currentTarget.dataset.index;
    if (this.data.tabActive === 1) {
      this.setData({
        zcCategorySelectedIndex: index
      })
    } else {
      this.setData({
        srCategorySelectedIndex: index
      })
    }

  },
  //tabs切换
  onTabChange(e) {
    console.log("e", e.detail.index)
    this.setData({
      tabActive: e.detail.index
    })
  },
  onAddCatrgory(e) {
    let type = e.currentTarget.dataset.index;
    let bookId = this.data.bookId
    wx.navigateTo({
      url: '/pages/category/category?type=' + type + '&bookId=' + bookId,
      success: function (res) {
        // 通过eventChannel向被打开页面传送数据
        // res.eventChannel.emit('acceptDataFromOpenerPage', {
        //   type,
        //   bookId
        // })
      }
    })
  },
  // 记一笔的详情
  onMoneyDetail(e) {
    const thiz = this
    wx.navigateTo({
      url: '/pages/mark/editor/editor',
      success: function (res) {
        // 通过eventChannel向被打开页面传送数据
        res.eventChannel.emit('acceptDataFromOpenerPage', {
          data: thiz.data.detailDesc,
          businessId: thiz.data.initData.id
        })
      }
    })

  },
  onShareAppMessage() {
    return {
      title: '蜻蜓记账本，随心所记',
      path: '/pages/index/index',
    }
  }
})