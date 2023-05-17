import {
    formatTime
} from '../../utils/dateUtil'
import {
    profix,
    getRequest,
    postRequest
} from '../../utils/request'
var wxCharts = require('../../utils/wxcharts.js');
var lineChart = null;
var ringChart = null;
var width = 375;
var height = 250;
try {
    var res = wx.getSystemInfoSync();
    // 这个1%是wxcss里面的 margin:1vw 1%，所以要减去屏幕宽度的1%
    width = res.windowWidth - (res.windowWidth / 100);
} catch (e) {
    console.error('getSystemInfoSync failed!');
}
Page({

    /**
     * 页面的初始数据
     */
    data: {
        empty: false,
        //   {
        //     bookId: 0,
        //     time: new Date().getFullYear()
        // },
        // 选中的账本
        queryData: {
            bookId: 0,
            startTime: "2022-01-01",
            endTime: "2022-12-30",
            timeType: 2,
            type: 0,
            time: new Date().getFullYear() + '',
            yearTimeDesc: new Date().getFullYear() + ''
        },
        listData: null,
        // year month day
        fields: 'year',
        bookList: [],
        typeList: [{
            id: '0',
            text: '收入',
        }, {
            id: '1',
            text: '支出',
        }],
        btnList: [{
                id: '0',
                text: '周',
            },
            {
                id: '1',
                text: '月',

            }, {
                id: '2',
                text: '年',
            },
        ]
    },
    bindPickerChange: function (e) {
        this.setData({
            'queryData.bookId': e.detail.value
        })
        this.list()
    },
    bindDateChange: function (e) {

        this.setData({
            'queryData.time': e.detail.value,
            'queryData.yearTimeDesc': e.detail.value
        })
        this.list()
    },
    typeEvent(e) {

        this.setData({
            'queryData.type': e.detail.currentId
        })
        this.list()
    },
    // 选择年月日（0-周 1-月 2-年）
    timeEvent(e) {
        let fields = ''
        if (e.detail.currentId == '2') {
            // year month day ,不知道为什么picker选择器粒度是年的时候要加上 '-01-01' 不然下拉就是 1年，2年，3年
            fields = 'year'
            this.setData({
                fields,
                'queryData.time': new Date().getFullYear() + "-01-01",
                'queryData.timeType': '2',
                'yearTimeDesc': new Date().getFullYear(),
            })
        } else if (e.detail.currentId == '1') {
            fields = 'month'
            this.setData({
                fields,
                'queryData.time': new Date().getFullYear() + '-' + new Date().getMonth() + 1,
                'queryData.timeType': '1'
            })
        }
        if (e.detail.currentId == '0') {
            wx.showToast({
                title: '暂不支持周查询',
                icon: 'error'
            })

            return
        }
        this.list()
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {

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
        let time = new Date().getFullYear() + ''
        this.setData({
            'queryData.time': time,
            'yearTimeDesc': time.split("-")[0]
        })
        this.bookList()

    },
    bookList() {
        getRequest("/api/book/get").then(res => {
            this.setData({
                bookList: res.data
            })
            this.list()
        })
    },
    /**
     * 获取当传入时间年份的第一天与最后一天 
     * @param {Date对象} firstDay 
     * @return {startTime: "2023-12-30 23:59:59" ,  endTime: "2023-01-01 00:00:00"}
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
            startTime: firstDay,
            endTime: lastDay
        }
    },
    /**
     * 获取当传入时间月份的第一天与最后一天 
     * @param {Date对象} firstDay 
     * @return 
     */
    onMonthFirstLastDay(firstDay) {

        var lastDay = new Date(firstDay);
        lastDay.setMonth(lastDay.getMonth() + 1);
        lastDay.setDate(-1);
        firstDay = formatTime(firstDay, "YYYY-MM-dd 00:00:00");
        lastDay = formatTime(lastDay, "YYYY-MM-dd 23:59:59");
        return {
            startTime: firstDay,
            endTime: lastDay
        }
    },
    list() {
        let time = {}
        // timeType （0-周 1-月 2-年）
        if (this.data.queryData.timeType == '2') {
            time = this.onYearFirstLastDay(new Date(String(this.data.queryData.time)))
        } else if (this.data.queryData.timeType == '1') {
            time = this.onMonthFirstLastDay(new Date(String(this.data.queryData.time)))
        }
        console.log("firstDay", time)
        postRequest("/api/report/list", {
            ...this.data.queryData,
            // 将bookList下标转换成 账本id
            bookId: this.data.bookList[this.data.queryData.bookId].id,
            ...time
        }).then(res => {
            let categories = []
            if (res.status == 0) {
                this.setData({
                    listData: res.data
                })
                // 暂时只支持单选
                let result = res.data.categories
                let series = [];
                for (let i = 0; i < res.data.length; i++) {
                    const element = res.data[i];
                    let data = {}
                    data['name'] = element.bookName
                    data['data'] = element.data
                    series.push(data)
                }
                new wxCharts({

                    animation: true, //是否有动画
                    canvasId: 'lineCanvas',
                    type: 'line',
                    categories: res.data[0].col,
                    series,
                    yAxis: {
                        title: '金额',
                        format: function (val) {
                            return val;
                        }
                    },
                    width,
                    height,
                    dataLabel: true
                });
                for (let i = 0; i < res.data.length; i++) {
                    let pieSeries = []
                    for (let j = 0; j < res.data[i].col.length; j++) {
                        pieSeries.push({
                            name: res.data[i].col[j],
                            data: Number(res.data[i].data[j])
                        })
                    }
                    new wxCharts({
                        canvasId: 'pieCanvas',
                        type: 'pie',
                        series: pieSeries,
                        color: [
                            //  配置31种颜色，但是不生效
                            '#FFFFFF', '#F5F5F5', '#DCDCDC', '#D3D3D3', '#C0C0C0', '#A9A9A9', '#808080', '#696969', '#000000', '#FFFAFA', '#FF7F50',
                            '#45C2E0', '#C1EBDD', '#FFC851', '#5A5476', '#1869A0', '#FF9393', '#FFB6C1', '#0000FF', '#00FFFF', '#008080',
                            '#00FF7F', '#8FBC8F', '#BC8F8F', '#228B22', '#DEB887', '#008000', '#FFE4B5', '#FAF0E6', '#BDB76B', '#8B4513',

                        ],
                        width,
                        height,
                        dataLabel: true
                    });
                    pieSeries = []
                }
            } else {
                wx.showToast({
                    title: '数据为空',
                })

            }
        }).catch(err => {
            console.log("eeeeeeeeee", e)
            wx.showToast({
                title: err,
            })
        })
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