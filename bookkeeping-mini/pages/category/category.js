import {
    profix,
    getRequest,
    postRequest
} from '../../utils/request'
Page({
    /**
     * 页面的初始数据
     */
    data: {
        icon: '',
        form: null
    },
    onRemoveImage() {
        this.setData({
            icon: ''
        })
    },
    // 图标上传
    onUploadImage() {
        const thiz = this
        wx.chooseMedia({
            count: 1,
            success: function (response) {
                if (response.tempFiles[0].size > (200 * 1024)) {
                    wx.showToast({
                        title: '图标不能大于200KB',
                        icon: 'none'
                    })
                    return
                }
                wx.getFileSystemManager().readFile({
                    filePath: response.tempFiles[0].tempFilePath, //选择图片返回的相对路径
                    encoding: 'base64', //编码格式
                    success: readRes => { //成功的回调
                        thiz.setData({
                            icon: 'data:image/png;base64,' + readRes.data
                        })
                    },
                    fail: err => { //失败
                        wx.showToast({
                            title: '文件上传失败！',
                            icon: 'none'
                        })
                    }
                })

            }
        })
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.setData({
            form: options
        })
    },
    formSubmit(e) {
        // 分类名称
        let name = e.detail.value.name
        let icon = this.data.icon
        if (!name) {
            wx.showToast({
                title: '请填写分类名称',
                icon: 'error'
            })
            return
        }
        if (name.lenth > 5) {
            wx.showToast({
                title: '分类名称长度为5字符以内',
            })
        }
        if (!icon) {
            wx.showToast({
                title: '请上传分类图标',
                icon: 'error'
            })
            return
        }
        if (!icon) {
            wx.showToast({
                title: '页面失效！',
                icon: 'error'
            })
            return
        }
        postRequest("/api/category/save", {
            ...this.data.form,
            name,
            icon
        }).then(res => {
            if (res.status == 0) {
                // 返回到上个页面
                let pages = getCurrentPages();
                let beforePage = pages[pages.length - 2];
                //getNoteDetail为上一个页面的刷新数据函数；
                beforePage.init({
                    bookId: this.data.form.bookId
                });
                wx.navigateBack({
                    delta: 1
                });
            } else {
                wx.showToast({
                    title: res.msg,
                    icon: 'error'
                })
            }
        }).catch(err => {
            wx.showToast({
                title: err,
                icon: 'error'
            })
        })

    },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    }
})