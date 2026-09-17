<template>
  <footer class="site-footer">
    <div class="footer-container">
      <!-- 三列内容 -->
      <div class="footer-content">
        <!-- 关于我们 -->
        <div class="footer-column">
          <h4 class="column-title">关于我们</h4>
          <p class="column-text">
            寿阳文旅云是寿阳县官方文化旅游综合服务平台，致力于为游客和市民提供便捷的文旅资讯、在线预订、活动报名等一站式服务，让文化旅游触手可及。
          </p>
          <div class="footer-logo">
            <el-icon :size="20"><Picture /></el-icon>
            <span>寿阳文旅云</span>
          </div>
        </div>

        <!-- 联系方式 -->
        <div class="footer-column">
          <h4 class="column-title">联系方式</h4>
          <ul class="contact-list">
            <li>
              <el-icon><Phone /></el-icon>
              <span>服务热线：0354-12345678</span>
            </li>
            <li>
              <el-icon><Message /></el-icon>
              <span>邮箱：service@shouyang.gov.cn</span>
            </li>
            <li>
              <el-icon><Location /></el-icon>
              <span>地址：山西省晋中市寿阳县朝阳街</span>
            </li>
            <li>
              <el-icon><Clock /></el-icon>
              <span>工作时间：周一至周五 9:00-17:00</span>
            </li>
          </ul>
        </div>

        <!-- 快速链接 -->
        <div class="footer-column">
          <h4 class="column-title">快速链接</h4>
          <div class="quick-links">
            <router-link to="/news">文旅动态</router-link>
            <router-link to="/service/ticket">票务预订</router-link>
            <router-link to="/service/activity">活动报名</router-link>
            <router-link to="/culture/pavilion">数字展馆</router-link>
            <router-link to="/culture/heritage">非遗文化</router-link>
            <router-link to="/travel/scenic">景点推荐</router-link>
            <router-link to="/travel/food">特色美食</router-link>
            <router-link to="/travel/guide">旅游攻略</router-link>
            <router-link to="/org">文旅单位</router-link>
            <router-link to="/search">站内搜索</router-link>
          </div>
        </div>
      </div>

      <!-- 底部版权信息 -->
      <div class="footer-bottom">
        <div class="bottom-text">
          <span>© 2024 寿阳文旅云 版权所有</span>
          <span class="separator">|</span>
          <span>晋ICP备XXXXXXXX号</span>
          <span class="separator">|</span>
          <span>晋公网安备 XXXXXXXXXXXXX号</span>
        </div>
        <div class="bottom-text">
          <span>技术支持：寿阳县文化和旅游局</span>
          <span class="separator">|</span>
          <span>网站标识码：XXXXXXXXXX</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { Picture, Phone, Message, Location, Clock } from '@element-plus/icons-vue'
</script>

<style lang="scss" scoped>
.site-footer {
  background: #1a2a4a;
  color: #b0bec5;
  margin-top: 40px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 三列内容 */
.footer-content {
  display: flex;
  justify-content: space-between;
  padding: 40px 0 30px;
  gap: 40px;
}

.footer-column {
  flex: 1;
}

.column-title {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #4a6fa5;
  display: inline-block;
}

.column-text {
  font-size: 13px;
  line-height: 1.8;
  color: #90a4ae;
  margin-bottom: 16px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;

  .el-icon {
    color: #4a6fa5;
  }
}

/* 联系方式 */
.contact-list {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    display: flex;
    align-items: flex-start;
    gap: 10px;
    margin-bottom: 14px;
    font-size: 13px;
    color: #90a4ae;
    line-height: 1.5;

    .el-icon {
      margin-top: 2px;
      color: #4a6fa5;
      flex-shrink: 0;
    }
  }
}

/* 快速链接 */
.quick-links {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 16px;

  a {
    color: #90a4ae;
    text-decoration: none;
    font-size: 13px;
    transition: color 0.2s;

    &:hover {
      color: #fff;
    }
  }
}

/* 底部版权 */
.footer-bottom {
  border-top: 1px solid #2c3e6b;
  padding: 20px 0;
  text-align: center;
}

.bottom-text {
  font-size: 12px;
  color: #607d8b;
  line-height: 2;

  .separator {
    margin: 0 10px;
    color: #37474f;
  }
}

/* 响应式 */
@media (max-width: 992px) {
  .footer-content {
    flex-direction: column;
    gap: 30px;
  }
}

@media (max-width: 576px) {
  .bottom-text {
    .separator {
      display: none;
    }

    span {
      display: block;
      margin: 4px 0;
    }
  }
}
</style>
