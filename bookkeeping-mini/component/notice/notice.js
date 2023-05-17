Component({
  /**
   * 组件的属性列表
   */
  properties: {
    notice:{
      
    }
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
    navigateToURL (e) {
      console.log(e);
      let elements = this.data.elements;
        let navigateURL = e.currentTarget.dataset.url;
        wx.navigateTo({
          url: '/component/notice/web-view/web-view?url=' + navigateURL,
          success: (result) => {},
          fail: (res) => {},
          complete: (res) => {},
        })
    },
  }
})