# 关于maven找不到spire.xls.free包的问题

### 由于excel转图片使用到了微软第三方spire.xls.free包，但是作者搜索了maven仓库，实在是找不到坐标：spire.xls.free， 所以，我找了本地的jar，因此大家需要导入maven依赖

```
    <dependency>
            <groupId>e-iceblue</groupId>
            <artifactId>spire.xls.free</artifactId>
            <version>2.2.0</version>
        </dependency>
```
jar包放在了项目根目录下，只需要修改-Dfile为你指定的项目拉去目录即可（我的项目路径E:\workpath\）
```
mvn install:install-file -Dfile=E:\workpath\book-dragonfly\spire.xls.free-2.2.0.jar -DgroupId=e-iceblue -DartifactId=spire.xls.free -Dversion=2.2.0 -Dpackaging=jar
```