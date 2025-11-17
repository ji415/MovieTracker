## 一、项目目标
实现一个 电影推荐与追踪系统 (Movie Recommendation & Tracker)，允许用户：
- 登录（基于用户名和密码）
- 管理个人观影清单（watchlist）
- 查看观看历史（history）
- 获取推荐电影（recommendations）
---

## 二、主要功能
| 功能      | 描述                          |
| ------- | --------------------------- |
| 登录/退出   | 用户用用户名密码登录系统                |
| 浏览电影    | 从 CSV 文件中读取所有电影列表           |
| 添加/移除清单 | 添加或删除个人 watchlist 中的电影      |
| 查看清单    | 查看 watchlist 中所有电影          |
| 标记已观看   | 将电影添加到观看历史，并从 watchlist 中移除 |
| 查看历史    | 查看所有已观看的电影                  |
| 获取推荐    | 根据观看历史推荐电影（如最常看的类型）         |

---
## 三、数据文件（CSV）
1. movies.csv：ID, Title, Genre, Year, Rating
2. users.csv：Username, Password, Watchlist, History

---
## 四、面向对象设计
```
Movie
 ├── id: int
 ├── title: String
 ├── genre: String
 ├── year: int
 └── rating: double

User
 ├── username: String
 ├── password: String
 ├── watchlist: ArrayList<Movie>
 ├── history: ArrayList<Movie>

Watchlist
 ├── movies: ArrayList<Movie>
 ├── addMovie(Movie)
 ├── removeMovie(Movie)
 ├── listMovies()

History
 ├── movies: ArrayList<Movie>
 ├── addMovie(Movie)
 ├── listMovies()

RecommendationEngine
 ├── recommend(User user, int n)
 └── 基于 genre 或 rating 推荐电影

Main (入口类)
 ├── mainMenu()
 ├── loginMenu()
 ├── userMenu()
```
---
## 五、技术要求
- 使用Java标准库： ArrayList, HashMap, File, Scanner, BufferedReader
- 实现异常处理（文件找不到、输入错误等）
- 禁止使用外部库
- 进阶功能
   - 注册新用户
   - 修改密码
   - 多种推荐策略
   - GUI（JavaFX）
   - 密码加密保存
   - 用户分级（BasicUser, PremiumUser）
   - 电影分类（FeatureFilm, Documentary）









