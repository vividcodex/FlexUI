import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.jetbrains.compose)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.android.library)
	alias(libs.plugins.maven.publish)
}

val flexUiVersion = property("flex-ui.version").toString()

group = "cn.vividcode.multiplatform.flex.ui.api"
version = flexUiVersion

kotlin {
	jvmToolchain(21)
	
	androidTarget {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
		}
	}
	
	jvm("desktop") {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
		}
	}
	
	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.binaries.framework {
			baseName = "FlexUi"
			isStatic = true
		}
	}
	
	js {
		moduleName = "flexUi"
		browser {
			commonWebpackConfig {
				outputFileName = "flexUi.js"
			}
		}
		binaries.executable()
		useEsModules()
	}
	
	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		moduleName = "flexUi"
		browser {
			val rootDirPath = project.rootDir.path
			val projectDirPath = project.projectDir.path
			commonWebpackConfig {
				outputFileName = "flexUi.js"
				devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
					static = (static ?: mutableListOf()).apply {
						add(rootDirPath)
						add(projectDirPath)
					}
				}
			}
		}
		binaries.executable()
	}
	
	sourceSets {
		commonMain {
			dependencies {
				implementation(compose.runtime)
				implementation(compose.foundation)
				implementation(compose.material3)
				implementation(compose.ui)
				implementation(compose.components.resources)
				implementation(compose.components.uiToolingPreview)
			}
			kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
		}
		androidMain {
			dependencies {
			
			}
		}
		val desktopMain by getting
		desktopMain.dependencies {
		
		}
		iosMain {
			dependencies {
			
			}
		}
	}
}

android {
	namespace = "cn.vividcode.multiplatform.flex.ui"
	compileSdk = libs.versions.android.compileSdk.get().toInt()
	
	sourceSets["main"].apply {
		manifest.srcFile("src/androidMain/AndroidManifest.xml")
		res.srcDirs("src/androidMain/res")
		resources.srcDirs("src/commonMain/resources")
	}
	
	defaultConfig {
		minSdk = libs.versions.android.minSdk.get().toInt()
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
			merges += "/META-INF/DEPENDENCIES"
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}
}

fun String.isReleaseVersion(): Boolean {
	val versionRegex = """^\d+\.\d+\.\d+(-(exp|alpha|beta|rc)-\d{2})?$""".toRegex()
	check(this.matches(versionRegex)) {
		"""
			$this 是一个非法的版本号
			以下为正确实例：
			1. 1.0.0-exp-01     (早期开发阶段)
			2. 1.0.0-alpha-01   (组件确定，会修改功能)
			3. 1.0.0-beta-01    (功能确定，主要以修复为主)
			4. 1.0.0-rc-01      (最终错误修复，正式版前推出版本)
			5. 1.0.0            (正式版本)
		""".trimIndent()
	}
	val releaseRegex = "-(exp|alpha|beta|rc)-\\d{2}$".toRegex()
	return !this.matches(releaseRegex)
}

mavenPublishing {
	val release = flexUiVersion.isReleaseVersion()
	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, release)
	signAllPublications()
	
	coordinates("cn.vividcode.multiplatform", "flex-ui", flexUiVersion)
	
	pom {
		name.set("flex-ui")
		description.set("FlexUi 基于 Kotlin Multiplatform 的 UI 组件库")
		inceptionYear.set("2025")
		url.set("https://github.com/vividcodex/flex-ui")
		licenses {
			license {
				name.set("The Apache License, Version 2.0")
				url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
				distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
			}
		}
		developers {
			developer {
				id.set("li-jia-wei")
				name.set("li-jia-wei")
				url.set("https://github.com/vividcodex/flex-ui")
			}
		}
		
		scm {
			url.set("https://github.com/vividcodex/flex-ui")
			connection.set("scm:git:git://github.com/vividcodex/flex-ui.git")
			developerConnection.set("scm:git:ssh://git@github.com:vividcodex/flex-ui.git")
		}
	}
}