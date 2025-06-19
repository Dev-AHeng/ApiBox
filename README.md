# ApiBox - Android API集合工具箱

## 项目介绍

ApiBox是一个功能丰富的Android应用程序，集成了多种实用的API服务和工具。该项目采用模块化架构，包含多个功能模块，为用户提供便捷的API访问和数据查询服务。

## 功能特性

### 🎯 主要功能模块

#### 1. 视频相关功能
- **抖音视频解析** - 解析抖音视频链接，获取无水印视频
- **聚合短视频解析** - 支持多平台短视频解析
- **视频播放器** - 自定义视频播放器，支持手势控制

#### 2. 图片相关功能
- **必应每日壁纸** - 获取必应7天内的精美壁纸
- **随机二次元图片** - 随机获取动漫图片
- **图片查看器** - 高质量图片浏览体验

#### 3. 查询工具
- **IP地址查询** - 查询IP地址的详细信息和归属地
- **本地IP查询** - 获取设备本地IP信息
- **ICP备案查询** - 查询网站备案信息
- **条码查询** - 扫描或输入条码获取商品信息
- **QQ账号信息查询** - 查询QQ用户的公开信息

#### 4. 热搜榜单
- **多平台热搜** - 聚合各大平台的热搜榜单
  - 微博热搜
  - 百度热搜
  - 知乎热搜
  - 抖音热搜
  - 等更多平台

#### 5. 游戏相关
- **王者荣耀英雄强度排行** - 查看英雄强度排行榜
- **英雄详细信息** - 获取英雄的详细数据和信息

#### 6. 音乐功能
- **咪咕音乐VIP** - 音乐相关功能
- **音乐播放服务** - 后台音乐播放支持

#### 7. 其他实用工具
- **二维码生成** - 生成各种类型的二维码
- **网站TDK查询** - 查询网站的标题、描述、关键词
- **一言API** - 获取随机的优美句子
- **浏览器功能** - 内置网页浏览器

## 技术架构

### 模块结构
```
ApiBox/
├── app/                    # 主应用模块
├── common/                 # 公共模块 (基础组件、工具类)
├── gsyvideoplayer/         # 视频播放器模块
├── model_guiguiapi/        # GUI API模块
├── model_sanhaiapi/        # 三海API模块
└── module_user/           # 用户模块
```

### 技术栈
- **开发语言**: Java, Kotlin
- **UI框架**: Android原生 + Material Design 3
- **网络请求**: Retrofit + OkHttp
- **图片加载**: Glide
- **数据绑定**: Android DataBinding
- **架构模式**: MVVM + LiveData
- **依赖注入**: 手动依赖注入
- **视频播放**: GSYVideoPlayer
- **数据存储**: SharedPreferences + Protocol Buffers

## 环境要求

- **Android Studio**: Arctic Fox 及以上版本
- **Gradle**: 7.0+
- **Android SDK**: API Level 21+ (Android 5.0+)
- **JDK**: 11+

## 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/Dev-AHeng/ApiBox.git
cd ApiBox
```

### 2. 导入项目
使用Android Studio打开项目根目录

### 3. 同步依赖
等待Gradle同步完成

### 4. 运行项目
连接Android设备或启动模拟器，点击运行按钮

## 项目结构说明

### 主要目录
- `app/` - 主应用模块，包含启动页面和主要导航
- `common/` - 公共库，包含基础Activity、工具类、通用UI组件
- `gsyvideoplayer/` - 视频播放相关功能
- `model_guiguiapi/` - GUI相关的API功能模块
- `model_sanhaiapi/` - 三海API相关的功能模块
- `module_user/` - 用户相关功能模块

### 关键文件
- `build.gradle.kts` - 项目级别的构建配置
- `gradle/libs.versions.toml` - 版本目录管理
- `settings.gradle.kts` - 项目设置和模块配置

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目地址: [https://github.com/Dev-AHeng/ApiBox](https://github.com/Dev-AHeng/ApiBox)
- 问题反馈: [Issues](https://github.com/Dev-AHeng/ApiBox/issues)

## 更新日志

### v1.0.0 (2024-12-19)
- 🎉 初始版本发布
- ✨ 完整的模块化架构
- 🚀 多种API功能集成
- 📱 Material Design 3 UI

## 致谢

感谢所有为这个项目做出贡献的开发者和提供API服务的平台。

---

⭐ 如果这个项目对您有帮助，请给它一个星标！ 