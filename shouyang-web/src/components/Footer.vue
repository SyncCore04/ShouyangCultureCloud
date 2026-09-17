<template>
  <footer class="site-footer">
    <div class="footer-inner container">
      <div class="footer-content">
        <!-- 关于我们 -->
        <div class="footer-section">
          <h4 class="footer-title">关于我们</h4>
          <p class="footer-desc">
            寿阳文旅云是寿阳县公共文旅服务平台，致力于为广大群众提供便捷、高效的文化和旅游服务，推动文旅融合发展。
          </p>
        </div>

        <!-- 快速链接 -->
        <div class="footer-section">
          <h4 class="footer-title">快速链接</h4>
          <ul class="footer-links">
            <li><router-link to="/news">文旅动态</router-link></li>
            <li><router-link to="/service/activity">活动报名</router-link></li>
            <li><router-link to="/culture/heritage">非遗文化</router-link></li>
            <li><router-link to="/travel/scenic">景点推荐</router-link></li>
          </ul>
        </div>

        <!-- 联系方式 -->
        <div class="footer-section">
          <h4 class="footer-title">联系方式</h4>
          <ul class="footer-contact">
            <li>
              <el-icon><Location /></el-icon>
              <span>山西省晋中市寿阳县</span>
            </li>
            <li>
              <el-icon><Phone /></el-icon>
              <span>0354-1234567</span>
            </li>
            <li>
              <el-icon><Message /></el-icon>
              <span>sywenlv@example.com</span>
            </li>
            <li>
              <el-icon><Clock /></el-icon>
              <span>周一至周五 8:30-17:30</span>
            </li>
          </ul>
        </div>

        <!-- 关注我们 -->
        <div class="footer-section">
          <h4 class="footer-title">关注我们</h4>
          <div class="qrcode-placeholder">
            <el-icon :size="48" color="#90a4ae"><Picture /></el-icon>
            <p>扫码关注公众号</p>
          </div>
        </div>
      </div>

      <!-- 底部版权 -->
      <div class="footer-bottom">
        <p>Copyright © 2024 寿阳文旅云 版权所有</p>
        <p>
          <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener">京ICP备11017824号-4</a>
          <span class="sep">|</span>
          技术支持：超星集团
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup>
// 页脚组件
</script>

<style lang="scss" scoped>
.site-footer {
  background: linear-gradient(135deg, $primary-dark 0%, #0f1a2e 100%);
  color: rgba(255, 255, 255, 0.75);
  margin-top: 40px;
}

.footer-inner {
  padding: 40px 16px 0;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1.5fr 1fr;
  gap: 40px;
  padding-bottom: 32px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-section {
  .footer-title {
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    position: relative;
    padding-bottom: 8px;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      width: 30px;
      height: 2px;
      background: $primary-light;
    }
  }

  .footer-desc {
    font-size: 13px;
    line-height: 1.8;
    color: rgba(255, 255, 255, 0.6);
  }
}

.footer-links {
  li {
    margin-bottom: 10px;

    a {
      color: rgba(255, 255, 255, 0.6);
      font-size: 13px;
      transition: color $transition-fast;

      &:hover {
        color: #fff;
      }
    }
  }
}

.footer-contact {
  li {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.6);

    .el-icon {
      color: $primary-light;
      flex-shrink: 0;
    }
  }
}

.qrcode-placeholder {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: $border-radius;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;

  p {
    font-size: 11px;
    color: rgba(255, 255, 255, 0.5);
  }
}

.footer-bottom {
  padding: 20px 0;
  text-align: center;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);

  p {
    margin-bottom: 4px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  a {
    color: rgba(255, 255, 255, 0.4);

    &:hover {
      color: rgba(255, 255, 255, 0.7);
    }
  }

  .sep {
    margin: 0 8px;
  }
}
</style>
