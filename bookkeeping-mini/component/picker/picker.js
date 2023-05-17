// rmtjPage/pages/components/rmtj-select/rmtj-select.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    required: {
      type: Boolean,
      value: false
    },
    rangeKey: {
      type: String,
      value: 'label'
    },
    rangeValue: {
      type: String,
      value: 'id'
    },
    range: {
      type: Array,
      value: [{
        label: '中国',
        value: "1"
      }, {
        label: '美国',
        value: "2"
      }, {
        label: '日本',
        value: "3"
      }],
    },
    placeholder: {
      value: "请选择",
      type: String
    },
    bindchange: {

    },
    defaultValue: {
      value: null,
      type: String,
      optionalTypes: [Number]
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    isOpen: false,
    searchListtData: [],
    index: 0,
    inputVal: "",
    currentIndex: [0],
    isSearchResult: false,
    isPlaceholder: true,
    defaultTxt: '',
  },

  ready() {},
  observers: {

  },
  /**
   * 组件的方法列表
   */
  methods: {
    confirm() {
      let index = this.data.currentIndex[0]
      let item = this.data.searchListtData.length != 0 ? this.data.searchListtData[index] : this.data.range[index]
      this.triggerEventConfirm(item)
      this.closeBox()
    },
    triggerEventConfirm(item) {
      this.triggerEvent("confirm", {
        item
      }, {
        bubbles: false, // 事件是否冒泡
        // 事件是否可以穿越组件边界，为 false 时，事件只在引用组件的节点树上触发，
        // 不进入其他任何组件的内部
        composed: false,
        capturePhase: false // 事件是否拥有捕获阶段 
      })
    },
    closeBox() {
      this.setData({
        isOpen: false
      })
    },
    clickFn() {
      let index = 0
      this.data.currentIndex[0] = index
      this.setData({
        isOpen: true,
        currentIndex: this.data.currentIndex,
        isSearchResult: false,
        inputVal: "",
      })

    },
    bindChange(e) {
      this.data.currentIndex = e.detail.value
      this.setData({
        currentIndex: this.data.currentIndex
      })
    },
    selectFn(e) {
      console.log("eee", e)
      // this.triggerEvent("selectItem", e.currentTarget.dataset.item)
      this.triggerEventConfirm(e.currentTarget.dataset.item)
      this.setData({
        isOpen: false
      })
    },
    iptSearch(e) {
      if (!e.detail.value) {
        this.setData({
          isSearchResult: false
        })
      }
      this.setData({
        inputVal: e.detail.value,
      })
    },
    searchFn() {
      let arr = this.data.range.filter(item => {
        if (item[this.data.rangeKey].includes(this.data.inputVal))
          return item
      })
      console.log("")
      this.setData({
        searchListtData: arr,
        isSearchResult: true
      })

    },
    clearIpt() {
      this.setData({
        inputVal: "",
        isSearchResult: false
      })
    }
  }
})