export PKG_CONFIG_PATH=/Library/Frameworks/Mono.framework/Versions/2.8/lib/pkgconfig
gmcs -debug+ -r:Apache.NMS.dll,Ninject.dll,Ninject.Extensions.Wcf.dll -pkg:wcf server.cs i_domas_service.cs domas_service.cs system_config.cs unparsed_queue_manager.cs

mono server.exe
