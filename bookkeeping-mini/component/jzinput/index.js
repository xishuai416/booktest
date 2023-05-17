// components/jz-input.js
let c = require('../../utils/common.js');
Component({
  lifetimes: {
    attached: function () {
      // 在组件实例进入页面节点树时执行
      // this.init();
    },
  },
  //这里订阅是为了防止父组件数据传递过来慢了，已经执行过attached，数据不更新的问题
  observers: {
    'userData': function (initValue) {
      this.initUserData();
    },
    'initUserInfo': function (initValue) {
      // 将userData转换成 hashMap
      this.initUserInfo();
    },
    'initData': function (initValue) {
      // 在 value被设置时，执行这个函数
      if (JSON.stringify(initValue) != '{}') {
        // 修改
        this.init()
      } else {
        // 新增
        this.initDate(new Date());
      }

    }
  },
  // 以下是旧式的定义方式，可以保持对 <2.2.3 版本基础库的兼容
  attached: function () {
    // 在组件实例进入页面节点树时执行
    // this.init();
  },
  /**
   * 组件的属性列表
   */
  properties: {
    initData: {
      type: Object,
      value: {}
    },
    initDate: {
      type: String,
      value: ''
    },
    initRemark: {
      type: String,
      value: ''
    },
    initValue: {
      type: String,
      value: ''
    },
    initUserInfo: {},
    initUserId: {

    },
    initUserName: {

    },
    // 用户的列表
    userData: {
      type: Array,
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    userMap: null,
    // 选中的用户对象
    selectData: {},
    accountSelectedIndex: 3,
    day: '',
    remark: '',
    date: '',
    money: '',
    userName: '',
    show: false,
    bookTime: new Date().getTime(),
    formatter(type, value) {
      if (type === 'year') {
        return `${value}年`;
      } else if (type === 'month') {
        return `${value}月`;
      }
      return value;
    },
  },

  /**
   * 组件的方法列表
   */
  methods: {
    initUserInfo() {
      console.log("11212132", this.data.initUserInfo)
      console.log("userData", this.data.userData)
      let initUserName = this.data.initUserInfo.initUserName
      let initUserId = this.data.initUserInfo.initUserId
      let userData = this.data.userData
      // 如果用户名称存在，用户id不存在，则证明是自行填写用户名
      if (initUserName && !initUserId) {
        this.setData({
          selectData: {
            userName: initUserName,
            userId: initUserId
          }
        })
      } else {
        // 1、用户id存在(用户名称就存在)
        if (initUserId) {
          if (userData && JSON.stringify(this.data.selectData) == '{}') {
            userData.forEach((user, index) => {
              if (user.id == initUserId) {
                this.setData({
                  selectData: user
                })
              }
            })
          }
        } else {
          // 2、用户id不存在，用户名也不存在
          let id = wx.getStorageSync('userInfo').id
          console.log
          if (userData && JSON.stringify(this.data.selectData) == '{}') {
            userData.forEach((user, index) => {
              if (user.userId == id) {
                this.setData({
                  selectData: user
                })
              }
            })
          }
        }
      }
    },
    initUserData() {
      return;
      // 如果用户名称存在，用户id不存在，则证明是自行填写用户名
      if (this.data.initUserName && !this.data.initUserId) {
        this.setData({
          selectData: {
            userName: this.data.initUserName,
            userId: this.data.initUserId
          }
        })
      } else {
        // 1、用户id存在(用户名称就存在)
        // 2、用户id不存在，用户名也不存在
        let id = this.data.initUserId ? this.data.initUserId : wx.getStorageSync('userInfo').id
        if (this.data.userData && JSON.stringify(this.data.selectData) == '{}') {
          this.data.userData.forEach((user, index) => {
            if (user.userId == id) {
              this.setData({
                selectData: user
              })
            }
          })
        }
      }
    },
    bindConfirm: function (e) {
      this.setData({
        userName: '',
        selectData: e.detail.item
      })
    },
    init() {

      var cdate = this.data.initData.bookTime
      // 初始化时间
      if (cdate == null) {
        cdate = new Date();
      } else {
        cdate = new Date(cdate);
      }
      let cday = cdate.getDate();
      this.setData({
        remark: this.data.initData.remark,
        day: cday,
        bookTime: cdate.getTime()
      })
    },
    //初始化日期
    initDate: function (cdate) {
      if (cdate == null) {
        cdate = new Date();
      }
      let strDate = c.formatDate("y-m-d", cdate.getTime());
      let cday = cdate.getDate();
      this.setData({
        day: cday,
        date: strDate
      })
    },
    //数字变换
    numInput: function (e) {
      let type = e.target.dataset.type;
      if (type === 'num') {
        let num = e.target.dataset.num;
        //判断是不是点
        if (num === '.') {
          if (this.data.money === '') {
            this.setData({
              money: '0.'
            })
          } else if (this.data.money.indexOf('.') != -1) {
            //不允许输入多个点
          } else {
            this.setData({
              money: this.data.money + num
            })
          }
        } else {
          //如果第一个字符为0，现在输入第二个字符，则自动变为0.
          if (this.data.money === '0' && num !== '.') {
            this.setData({
              money: '0.' + num
            })
          } else {
            let length = this.data.money.length;
            let index = this.data.money.indexOf('.');
            //这是不让他输入太多位小数，默认为2位
            if (index != -1 && (length - index) > 2) {
              return;
            }
            this.setData({
              money: this.data.money + num
            })
          }
        }
        //判断删除操作
      } else if (type === 'del' && this.data.money !== '') {
        if (this.data.money === '0.') {
          this.setData({
            money: ''
          })
        } else {
          this.setData({
            money: this.data.money.substring(0, (this.data.money.length - 1))
          })
        }
      } else if (type === 'empty') {
        this.setData({
          money: ''
        })
      }
      var myEventDetail = {
        money: this.data.money
      } // detail对象，提供给事件监听函数
      var myEventOption = {} // 触发事件的选项
      this.triggerEvent('numChange', myEventDetail, myEventOption)
    },
    // 自定义输入用户名字
    onUserName(e) {
      this.data.userName = e.detail.value
    },
    //确定
    ok: function () {
      var myEventDetail = {
        money: this.data.money,
        date: this.data.date,
        remark: this.data.remark
      } // detail对象，提供给事件监听函数
      var userName = this.data.userName;
      if (userName != '') {
        myEventDetail = {
          ...this.data.initData,
          ...myEventDetail,
          userName,
          bookTime: c.formatDate("y-m-d", this.data.bookTime)
        }
      } else {
        // 选择器选到的用户
        myEventDetail = {
          ...this.data.initData,
          ...myEventDetail,
          userName: this.data.selectData.userName,
          userId: this.data.selectData.id,
          bookTime: c.formatDate("y-m-d", this.data.bookTime)
        }
      }
      var myEventOption = {} // 触发事件的选项
      this.triggerEvent('submit', myEventDetail, myEventOption)
    },
    dateInput(event) {
      this.initDate(new Date(event.detail))
      this.onClose();
    },
    showDateSelect() {
      this.setData({
        show: true
      });
    },
    //关闭选择日期
    onClose() {
      this.setData({
        show: false
      });
    },
    remarkInput(e) {
      this.data.remark = e.detail.value
    }
  }
})