var seckill = {

    api_name: '/api/promo/seckill',

    // 查看全部商品
    findAll() {
        return request({
            url: this.api_name + '/findAll',
            method: 'get'
        })
    },

    // 获取秒杀商品
    getSeckillGoods(skuId) {
        return request({
            url: this.api_name + '/getSeckillGoods/' + skuId,
            method: 'get'
        })
    },

    // 获取秒杀下单码
    getSeckillSkuIdStr(skuId) {
        return request({
            url: this.api_name + '/auth/getSeckillSkuIdStr/' + skuId,
            method: 'get'
        })
    },

    // 下单
    seckillOrder(skuId, skuIdStr) {
        return request({
            url: this.api_name + '/auth/seckillOrder/' + skuId + '?skuIdStr=' + skuIdStr,
            method: 'post'
        })
    },

    // 查询订单
    checkOrder(skuId) {
        return request({
            url: this.api_name + '/auth/checkOrder/' + skuId,
            method: 'get'
        })
    },

    // 提交订单
    submitOrder(order) {
        return request({
            url: this.api_name + '/auth/submitOrder',
            method: 'post',
            data: order
        })
    },
}
