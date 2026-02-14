<template>
  <div class="statistics">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h2 class="page-title">数据统计</h2>
          <p class="page-subtitle">查看酒店运营数据与业务分析</p>
        </div>
        <div class="header-actions">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            class="date-picker"
          />
        </div>
      </div>
    </div>
    
    <div class="stat-cards">
      <div class="stat-card rooms">
        <div class="card-icon">
          <el-icon :size="28"><OfficeBuilding /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ statsData.totalRooms || 0 }}</div>
          <div class="card-label">总客房数</div>
        </div>
        <div class="card-trend up">
          <el-icon><TrendCharts /></el-icon>
        </div>
      </div>
      
      <div class="stat-card booked">
        <div class="card-icon">
          <el-icon :size="28"><Document /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ statsData.bookedRooms || 0 }}</div>
          <div class="card-label">已预订客房</div>
        </div>
        <div class="card-trend up">
          <el-icon><TrendCharts /></el-icon>
        </div>
      </div>
      
      <div class="stat-card rate">
        <div class="card-icon">
          <el-icon :size="28"><DataAnalysis /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ statsData.bookingRate || 0 }}<span class="unit">%</span></div>
          <div class="card-label">预订率</div>
        </div>
        <div class="card-progress">
          <div class="progress-bar" :style="{ width: (statsData.bookingRate || 0) + '%' }"></div>
        </div>
      </div>
      
      <div class="stat-card revenue">
        <div class="card-icon">
          <el-icon :size="28"><Money /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">
            <span class="currency">¥</span>{{ formatMoney(statsData.totalRevenue || 0) }}
          </div>
          <div class="card-label">总营收</div>
        </div>
        <div class="card-trend up">
          <el-icon><TrendCharts /></el-icon>
        </div>
      </div>
    </div>
    
    <div class="charts-section">
      <div class="chart-row">
        <div class="chart-container">
          <div class="chart-header">
            <h3 class="chart-title">预订率统计</h3>
            <div class="chart-actions">
              <el-button-group size="small">
                <el-button :type="chartView.booking === 'pie' ? 'primary' : ''" @click="chartView.booking = 'pie'">饼图</el-button>
                <el-button :type="chartView.booking === 'bar' ? 'primary' : ''" @click="chartView.booking = 'bar'">柱状图</el-button>
              </el-button-group>
            </div>
          </div>
          <div ref="bookingChartRef" class="chart"></div>
        </div>
        
        <div class="chart-container">
          <div class="chart-header">
            <h3 class="chart-title">营收统计</h3>
          </div>
          <div ref="revenueChartRef" class="chart"></div>
        </div>
      </div>
      
      <div class="chart-row">
        <div class="chart-container">
          <div class="chart-header">
            <h3 class="chart-title">营收趋势</h3>
          </div>
          <div ref="revenueTrendRef" class="chart"></div>
        </div>
        
        <div class="chart-container">
          <div class="chart-header">
            <h3 class="chart-title">订单状态分布</h3>
          </div>
          <div ref="orderStatusRef" class="chart"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { OfficeBuilding, Document, TrendCharts, Money, DataAnalysis } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { adminStatsApi } from '@/api/admin'

const dateRange = ref([])
const statsData = ref({})
const bookingChartRef = ref(null)
const revenueChartRef = ref(null)
const revenueTrendRef = ref(null)
const orderStatusRef = ref(null)

const chartView = reactive({
  booking: 'pie'
})

let bookingChart = null
let revenueChart = null
let revenueTrendChart = null
let orderStatusChart = null
let bookingData = []

const initDateRange = () => {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 30)
  dateRange.value = [
    start.toISOString().slice(0, 10),
    end.toISOString().slice(0, 10)
  ]
}

const formatMoney = (value) => {
  if (value >= 10000) {
    return (value / 10000).toFixed(2) + '万'
  }
  return value.toFixed(2)
}

const getStats = async () => {
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    }
    
    const [bookingRes, revenueRes, trendRes, orderRes] = await Promise.all([
      adminStatsApi.getBookingRate(params),
      adminStatsApi.getRevenue(params),
      adminStatsApi.getRevenueTrend(params),
      adminStatsApi.getOrderStats(params)
    ])
    
    if (bookingRes.code === 200) {
      statsData.value = {
        ...statsData.value,
        totalRooms: bookingRes.data.totalRooms,
        bookedRooms: bookingRes.data.bookedRooms,
        bookingRate: bookingRes.data.bookingRate
      }
      bookingData = bookingRes.data.details || []
      renderBookingChart(bookingData)
    }
    
    if (revenueRes.code === 200) {
      statsData.value = {
        ...statsData.value,
        totalRevenue: revenueRes.data.totalRevenue
      }
      renderRevenueChart(revenueRes.data.details || [])
    }
    
    if (trendRes.code === 200) {
      renderRevenueTrendChart(trendRes.data || [])
    }
    
    if (orderRes.code === 200) {
      renderOrderStatusChart(orderRes.data || [])
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const renderBookingChart = (data) => {
  if (!bookingChartRef.value) return
  
  if (!bookingChart) {
    bookingChart = echarts.init(bookingChartRef.value)
  }
  
  const colors = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899']
  
  let option
  if (chartView.booking === 'pie') {
    option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}间 ({d}%)',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#E5E7EB',
        borderWidth: 1,
        textStyle: { color: '#374151' }
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'center',
        textStyle: { color: '#6B7280' }
      },
      series: [{
        name: '客房预订',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 3
        },
        label: {
          show: true,
          formatter: '{b}: {d}%',
          color: '#374151'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.2)'
          }
        },
        data: data.map((item, index) => ({
          name: item.typeName,
          value: item.booked,
          itemStyle: { color: colors[index % colors.length] }
        }))
      }]
    }
  } else {
    option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#E5E7EB',
        borderWidth: 1,
        textStyle: { color: '#374151' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: data.map(item => item.typeName),
        axisLine: { lineStyle: { color: '#E5E7EB' } },
        axisLabel: { color: '#6B7280' }
      },
      yAxis: {
        type: 'value',
        axisLine: { show: false },
        axisLabel: { color: '#6B7280' },
        splitLine: { lineStyle: { color: '#F3F4F6' } }
      },
      series: [{
        name: '预订数量',
        type: 'bar',
        barWidth: '50%',
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#3B82F6' },
            { offset: 1, color: '#1E40AF' }
          ])
        },
        data: data.map(item => item.booked)
      }]
    }
  }
  
  bookingChart.setOption(option)
}

const renderRevenueChart = (data) => {
  if (!revenueChartRef.value) return
  
  if (!revenueChart) {
    revenueChart = echarts.init(revenueChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      textStyle: { color: '#374151' },
      formatter: '{b}: ¥{c}'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.typeName),
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisLabel: { color: '#6B7280' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { 
        color: '#6B7280',
        formatter: '¥{value}'
      },
      splitLine: { lineStyle: { color: '#F3F4F6' } }
    },
    series: [{
      name: '营收',
      type: 'bar',
      barWidth: '50%',
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#10B981' },
          { offset: 1, color: '#059669' }
        ])
      },
      data: data.map(item => item.revenue)
    }]
  }
  
  revenueChart.setOption(option)
}

const renderRevenueTrendChart = (data) => {
  if (!revenueTrendRef.value) return
  
  if (!revenueTrendChart) {
    revenueTrendChart = echarts.init(revenueTrendRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      textStyle: { color: '#374151' },
      formatter: '{b}<br/>营收: ¥{c}'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map(item => item.date),
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisLabel: { color: '#6B7280' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { 
        color: '#6B7280',
        formatter: '¥{value}'
      },
      splitLine: { lineStyle: { color: '#F3F4F6' } }
    },
    series: [{
      name: '营收',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
          { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
        ])
      },
      lineStyle: {
        color: '#3B82F6',
        width: 3
      },
      itemStyle: {
        color: '#3B82F6',
        borderWidth: 2,
        borderColor: '#fff'
      },
      data: data.map(item => item.revenue)
    }]
  }
  
  revenueTrendChart.setOption(option)
}

const renderOrderStatusChart = (data) => {
  if (!orderStatusRef.value) return
  
  if (!orderStatusChart) {
    orderStatusChart = echarts.init(orderStatusRef.value)
  }
  
  const statusMap = {
    0: '待支付',
    1: '待确认',
    2: '已确认',
    3: '已入住',
    4: '已完成',
    5: '已取消',
    6: '已拒绝'
  }
  
  const colorMap = {
    0: '#F59E0B',
    1: '#F59E0B',
    2: '#3B82F6',
    3: '#3B82F6',
    4: '#10B981',
    5: '#6B7280',
    6: '#EF4444'
  }
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}单 ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      textStyle: { color: '#374151' }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      textStyle: { color: '#6B7280' }
    },
    series: [{
      name: '订单状态',
      type: 'pie',
      radius: ['40%', '65%'],
      center: ['60%', '50%'],
      itemStyle: {
        borderRadius: 6,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}: {c}',
        color: '#374151'
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.2)'
        }
      },
      data: data.map(item => ({
        name: statusMap[item.status],
        value: item.count,
        itemStyle: { color: colorMap[item.status] }
      }))
    }]
  }
  
  orderStatusChart.setOption(option)
}

watch(chartView, () => {
  renderBookingChart(bookingData)
})

const handleDateChange = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    getStats()
  }
}

const handleResize = () => {
  bookingChart?.resize()
  revenueChart?.resize()
  revenueTrendChart?.resize()
  orderStatusChart?.resize()
}

onMounted(async () => {
  initDateRange()
  await nextTick()
  getStats()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  bookingChart?.dispose()
  revenueChart?.dispose()
  revenueTrendChart?.dispose()
  orderStatusChart?.dispose()
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/main.scss';

.statistics {
  padding: 24px;
  
  .page-header {
    margin-bottom: 24px;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      
      .title-section {
        .page-title {
          font-size: 28px;
          font-weight: 700;
          color: $text-primary;
          margin: 0 0 8px;
          letter-spacing: -0.5px;
        }
        
        .page-subtitle {
          font-size: 14px;
          color: $text-secondary;
          margin: 0;
        }
      }
      
      .header-actions {
        .date-picker {
          :deep(.el-input__wrapper) {
            border-radius: 10px;
            box-shadow: none;
            border: 1px solid $border-color;
          }
        }
      }
    }
  }
  
  .stat-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;
    
    .stat-card {
      background: $white;
      border-radius: 16px;
      padding: 24px;
      display: flex;
      align-items: center;
      gap: 16px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
      border: 1px solid $border-light;
      position: relative;
      overflow: hidden;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
      }
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
      }
      
      &.rooms::before { background: $gradient-primary; }
      &.booked::before { background: $gradient-success; }
      &.rate::before { background: linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%); }
      &.revenue::before { background: $gradient-danger; }
      
      .card-icon {
        width: 56px;
        height: 56px;
        border-radius: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
      }
      
      &.rooms .card-icon { background: rgba($primary-color, 0.1); color: $primary-light; }
      &.booked .card-icon { background: rgba($success-color, 0.1); color: $success-color; }
      &.rate .card-icon { background: rgba($warning-color, 0.1); color: $warning-color; }
      &.revenue .card-icon { background: rgba($danger-color, 0.1); color: $danger-color; }
      
      .card-content {
        flex: 1;
        
        .card-value {
          font-size: 28px;
          font-weight: 700;
          color: $text-primary;
          line-height: 1;
          margin-bottom: 4px;
          
          .unit, .currency {
            font-size: 16px;
            font-weight: 600;
            margin-right: 2px;
          }
        }
        
        .card-label {
          font-size: 13px;
          color: $text-secondary;
        }
      }
      
      .card-trend {
        position: absolute;
        top: 16px;
        right: 16px;
        padding: 4px 8px;
        border-radius: 6px;
        font-size: 12px;
        
        &.up {
          background: rgba($success-color, 0.1);
          color: $success-color;
        }
      }
      
      .card-progress {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: $border-light;
        
        .progress-bar {
          height: 100%;
          background: linear-gradient(90deg, #F59E0B 0%, #FBBF24 100%);
          border-radius: 0 2px 2px 0;
          transition: width 0.5s ease;
        }
      }
    }
  }
  
  .charts-section {
    .chart-row {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
      margin-bottom: 20px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
    
    .chart-container {
      background: $white;
      border-radius: 16px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
      border: 1px solid $border-light;
      
      .chart-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        .chart-title {
          font-size: 16px;
          font-weight: 600;
          color: $text-primary;
          margin: 0;
        }
        
        .chart-actions {
          :deep(.el-button-group) {
            .el-button {
              padding: 6px 12px;
              border-radius: 0;
              
              &:first-child {
                border-radius: 6px 0 0 6px;
              }
              
              &:last-child {
                border-radius: 0 6px 6px 0;
              }
            }
          }
        }
      }
      
      .chart {
        height: 300px;
      }
    }
  }
}

@media (max-width: 1200px) {
  .statistics {
    .stat-cards {
      grid-template-columns: repeat(2, 1fr);
    }
    
    .charts-section .chart-row {
      grid-template-columns: 1fr;
    }
  }
}

@media (max-width: 768px) {
  .statistics {
    padding: 16px;
    
    .page-header .header-content {
      flex-direction: column;
      gap: 16px;
      
      .header-actions {
        width: 100%;
        
        .date-picker {
          width: 100%;
        }
      }
    }
    
    .stat-cards {
      grid-template-columns: 1fr;
    }
  }
}
</style>
