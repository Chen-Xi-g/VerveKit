import com.android.build.api.dsl.ApplicationExtension
import com.griffin.plugin.configureApplication
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * 可独立运行模块的插件
 *
 * @author 高国峰
 * @date 2024/3/17
 */
class ApplicationPlugin : Plugin<Project>{
    override fun apply(target: Project) {
        // 执行插件逻辑
        with(target){
            // 获取libs版本管理
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            // 插件管理器
            with(pluginManager){
                apply(libs.getPlugin("android-application"))
                apply(libs.getPlugin("jetbrains-kotlin-android"))
                apply(libs.getPlugin("android-kotlin-ksp"))
            }

            extensions.configure<ApplicationExtension>{
                configureApplication(libs, this)
            }
        }
    }

    private fun VersionCatalog.getPlugin(name: String) = this.findPlugin(name).get().get().pluginId
}

