//  用法：在你 点击事件的元素上添加 data-statu="open"属性，然后将 showModalStatus 传进来控制是否打开 
//  例：  组件：<drawers id="drawer" showModalStatus="{{showModalStatus}}"></drawers>
//  例：  调用：<button wx:for="{{item.bookList}}" wx:for-item="items" wx:key="idx" data-value="{{items}}" data-statu="open" bindtap="editMoney">打开</button>

// component/drawer/drawer.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    title: {
      value: '编辑'
    },
    showModalStatus: {
      type: Boolean,
      value: false
    },
    identifying:{

    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    animationData: {}
  },

  /**
   * 组件的方法列表
   */
  methods: {
    // 账本金额账单编辑弹出js

    // 编辑账本金额
    editMoney(e) {
      // 打开弹窗
      this.powerDrawer(e)
      // 获取 账本金额选中数据
      var money = e.currentTarget.dataset.value;
      console.log("编辑账本金额:", money)
    },
    // 打开弹窗
    open: function (e) {
     
      this.util("open")
    },
    close() {
      this.util("close")
    },
    // 点击遮罩层关闭
    powerDrawer: function (e) {
      var currentStatu = e.currentTarget.dataset.statu;
      this.util(currentStatu)
    },
    // 点击确认
    submit: function (e) {
      this.triggerEvent('submit',this.data.identifying);
    },
    util: function (currentStatu) {
      /* 动画部分 */
      // 第1步：创建动画实例  
      var animation = wx.createAnimation({
        duration: 200, //动画时长 
        timingFunction: "linear", //线性 
        delay: 0 //0则不延迟 
      });

      // 第2步：这个动画实例赋给当前的动画实例 
      this.animation = animation;

      // 第3步：执行第一组动画 
      animation.opacity(0).rotateX(-100).step();
      // 第4步：导出动画对象赋给数据对象储存 
      this.setData({
        animationData: animation.export()
      })

      // 第5步：设置定时器到指定时候后，执行第二组动画 
      setTimeout(function () {
        // 执行第二组动画 
        animation.opacity(1).rotateX(0).step();
        // 给数据对象储存的第一组动画，更替为执行完第二组动画的动画对象 
        this.setData({
          animationData: animation
        })

        //关闭 
        if (currentStatu == "close") {
          this.setData({
            showModalStatus: false
          });
        }
      }.bind(this), 200)

      // 显示 
      if (currentStatu == "open") {
        this.setData({
          showModalStatus: true
        });
      }
    }
  }
})