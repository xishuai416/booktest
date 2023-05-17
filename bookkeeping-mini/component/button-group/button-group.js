// button-group/button-group.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    labelKey: {
      type: String,
      value: ''
    },
    dataList: {
      type: Array,
      value: []
    },
    currentId: {
      type: String,
      value: ''
    },
  },

  /**
   * 组件的初始数据
   */
  data: {

  },

  /**
   * 组件的方法列表
   */
  methods: {
    switchBtn: function (e) {
      console.log(e.currentTarget.dataset.id, '子组件currentId')
      this.setData({
        currentId: e.currentTarget.dataset.id
      })
      var btnEventDetail = {
        currentId: e.currentTarget.dataset.id
      } // detail对象，提供给事件监听函数
      var btnEventOption = {} // 触发事件的选项
      this.triggerEvent('btnEvent', btnEventDetail, btnEventOption)
    }
  }
})