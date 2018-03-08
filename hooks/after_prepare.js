module.exports = function (context) {

    var path = context.requireCordovaModule('path'),
        fs = context.requireCordovaModule('fs'),
        crypto = context.requireCordovaModule('crypto'),
        Q = context.requireCordovaModule('q'),
        cordova_util = context.requireCordovaModule('cordova-lib/src/cordova/util'),
        platforms = context.requireCordovaModule('cordova-lib/src/platforms/platforms'),
        Parser = context.requireCordovaModule('cordova-lib/src/cordova/metadata/parser'),
        ParserHelper = context.requireCordovaModule('cordova-lib/src/cordova/metadata/parserhelper/ParserHelper'),
        ConfigParser = context.requireCordovaModule('cordova-common').ConfigParser;

    var deferral = new Q.defer();
    var projectRoot = cordova_util.cdProjectRoot();

    var key = crypto.randomBytes(24).toString('base64');
    var iv = crypto.randomBytes(12).toString('base64');
     // var key = 'h27CxZTdXP+0i2lrBO4a0k1ZIsboAIuC';
     // var iv = 'tDUkbIkNQKENoIv0';

    console.log('key=' + key + ', iv=' + iv)

    var targetFiles = loadCryptFileTargets();

    context.opts.platforms.filter(function (platform) {
        var pluginInfo = context.opts.plugin.pluginInfo;
        return pluginInfo.getPlatformsArray().indexOf(platform) > -1;

    }).forEach(function (platform) {
        var platformPath = path.join(projectRoot, 'platforms', platform);
        var platformApi = platforms.getPlatformApi(platform, platformPath);
        var platformInfo = platformApi.getPlatformInfo();
        var wwwDir = platformInfo.locations.www;

        findCryptFiles(wwwDir).filter(function (file) {
            return isCryptFile(file.replace(wwwDir, ''));
        }).forEach(function (file) {
            var content = fs.readFileSync(file, 'utf-8');
            fs.writeFileSync(file, encryptData(content, key, iv), 'utf-8');
            console.log('encrypt: ' + file);
        });

        if (platform == 'ios') {
            /*
            var pluginDir;
            try {
              var ios_parser = context.requireCordovaModule('cordova-lib/src/cordova/metadata/ios_parser'),
                  iosParser = new ios_parser(platformPath);
              pluginDir = path.join(iosParser.cordovaproj, 'Plugins', context.opts.plugin.id);
            } catch (err) {
              var xcodeproj_dir = fs.readdirSync(platformPath).filter(function(e) { return e.match(/\.xcodeproj$/i); })[0],
                  xcodeproj = path.join(platformPath, xcodeproj_dir),
                  originalName = xcodeproj.substring(xcodeproj.lastIndexOf(path.sep)+1, xcodeproj.indexOf('.xcodeproj')),
                  cordovaproj = path.join(platformPath, originalName);

              pluginDir = path.join(cordovaproj, 'Plugins', context.opts.plugin.id);
            }
            replaceCryptKey_ios(pluginDir, key, iv);
            */

        } else if (platform == 'android') {
            //var pluginDir = path.join(platformPath, 'src');
            var pluginDir = path.join(platformPath, 'app/src/main/java');
            replaceCryptKey_android(pluginDir, key, iv);

            var cfg = new ConfigParser(platformInfo.projectConfig.path);
            cfg.doc.getroot().getchildren().filter(function (child, idx, arr) {
                return (child.tag == 'content');
            }).forEach(function (child) {
                child.attrib.src = '/+++/' + child.attrib.src;
            });

            cfg.write();
        }
    });

    deferral.resolve();
    return deferral.promise;


    function findCryptFiles(dir) {
        var fileList = [];
        var list = fs.readdirSync(dir);
        list.forEach(function (file) {
            fileList.push(path.join(dir, file));
        });
        // sub dir
        list.filter(function (file) {
            return fs.statSync(path.join(dir, file)).isDirectory();
        }).forEach(function (file) {
            var subDir = path.join(dir, file)
            var subFileList = findCryptFiles(subDir);
            fileList = fileList.concat(subFileList);
        });

        return fileList;
    }

    function loadCryptFileTargets() {
        var xmlHelpers = context.requireCordovaModule('cordova-common').xmlHelpers;

        var pluginXml = path.join(context.opts.plugin.dir, 'plugin.xml');

        var include = [];
        var exclude = [];

        var doc = xmlHelpers.parseElementtreeSync(pluginXml);
        var cryptfiles = doc.findall('cryptfiles');
        if (cryptfiles.length > 0) {
            cryptfiles[0]._children.forEach(function (elm) {
                elm._children.filter(function (celm) {
                    return celm.tag == 'file' && celm.attrib.regex && celm.attrib.regex.trim().length > 0;
                }).forEach(function (celm) {
                    if (elm.tag == 'include') {
                        include.push(celm.attrib.regex.trim());
                    } else if (elm.tag == 'exclude') {
                        exclude.push(celm.attrib.regex.trim());
                    }
                });
            })
        }

        return { 'include': include, 'exclude': exclude };
    }

    function isCryptFile(file) {
        if (!targetFiles.include.some(function (regexStr) { return new RegExp(regexStr).test(file); })) {
            return false;
        }
        if (targetFiles.exclude.some(function (regexStr) { return new RegExp(regexStr).test(file); })) {
            return false;
        }
        return true;
    }

    function encryptData(input, key, iv) {
        var cipher = crypto.createCipheriv('aes-256-cbc', key, iv);
        var encrypted = cipher.update(input, 'utf8', 'base64') + cipher.final('base64');

        return encrypted;
    }

    function replaceCryptKey_ios(pluginDir, key, iv) {
        var sourceFile = path.join(pluginDir, 'CDVCryptURLProtocol.m');
        var content = fs.readFileSync(sourceFile, 'utf-8');

        var includeArrStr = targetFiles.include.map(function (pattern) { return '@"' + pattern.replace('\\', '\\\\') + '"'; }).join(', ');
        var excludeArrStr = targetFiles.exclude.map(function (pattern) { return '@"' + pattern.replace('\\', '\\\\') + '"'; }).join(', ');

        content = content.replace(/kCryptKey = @".*";/, 'kCryptKey = @"' + key + '";')
            .replace(/kCryptIv = @".*";/, 'kCryptIv = @"' + iv + '";')
            .replace(/kIncludeFiles\[\] = {.*};/, 'kIncludeFiles\[\] = { ' + includeArrStr + ' };')
            .replace(/kExcludeFiles\[\] = {.*};/, 'kExcludeFiles\[\] = { ' + excludeArrStr + ' };')
            .replace(/kIncludeFileLength = [0-9]+;/, 'kIncludeFileLength = ' + targetFiles.include.length + ';')
            .replace(/kExcludeFileLength = [0-9]+;/, 'kExcludeFileLength = ' + targetFiles.exclude.length + ';');

        fs.writeFileSync(sourceFile, content, 'utf-8');
    }

    function replaceCryptKey_android(pluginDir, key, iv) {
        var sourceFile = path.join(pluginDir, 'com/tottems/cordova/TottemsResource.java');
        var content = fs.readFileSync(sourceFile, 'utf-8');

        var includeArrStr = targetFiles.include.map(function (pattern) { return '"' + pattern.replace('\\', '\\\\') + '"'; }).join(', ');
        var excludeArrStr = targetFiles.exclude.map(function (pattern) { return '"' + pattern.replace('\\', '\\\\') + '"'; }).join(', ');


        var keypart1 = key.substring(0, 9);
        var keypart2 = key.substring(9, 21);
        var keypart3 = key.substring(21);

        var ivpart1 = iv.substring(0, 3);
        var ivpart2 = iv.substring(3, 10);
        var ivpart3 = iv.substring(10);

        var key1 = keypart1 + makerandomString(12) + makerandomString(10);
        var key2 = makerandomString(10) + keypart2 + makerandomString(10);
        var key3 = makerandomString(10) + makerandomString(12) + keypart3;

        var iv1 = ivpart1 + makerandomString(6) + makerandomString(6);
        var iv2 = makerandomString(4) + ivpart2 + makerandomString(6);
        var iv3 = makerandomString(4) + makerandomString(6) + ivpart3;

        console.log("Kye partido ",jg(key1, key2, key3) );
        console.log("Iv Partido ",  gh(iv1, iv2, iv3));

        content = content.replace(/var1 = ".*";/, 'var1 = "' + key + '";')
            .replace(/const1 = ".*";/, 'const1 = "' + iv + '";')
            .replace(/o1_ = ".*";/, 'o1_ = "' + key1 + '";')
            .replace(/o2_ = ".*";/, 'o2_ = "' + key2 + '";')
            .replace(/o3_ = ".*";/, 'o3_ = "' + key3 + '";')
            .replace(/o4_ = ".*";/, 'o4_ = "' + key.length + '";')
            .replace(/o5_ = ".*";/, 'o5_ = "' + jg(key1, key2, key3) + '";')
            .replace(/f1_ = ".*";/, 'f1_ = "' + iv1 + '";')
            .replace(/f2_ = ".*";/, 'f2_ = "' + iv2 + '";')
            .replace(/f3_ = ".*";/, 'f3_ = "' + iv3 + '";')
            .replace(/f4_ = ".*";/, 'f4_ = "' + iv.length + '";')
            .replace(/f5_ = ".*";/, 'f5_ = "' + gh(iv1, iv2, iv3) + '";')
            .replace(/INCLUDE_FILES = new String\[\] {.*};/, 'INCLUDE_FILES = new String[] { ' + includeArrStr + ' };')
            .replace(/EXCLUDE_FILES = new String\[\] {.*};/, 'EXCLUDE_FILES = new String[] { ' + excludeArrStr + ' };');

        fs.writeFileSync(sourceFile, content, 'utf-8');
    }

   function jg(o1_, o2_, o3_){
        return o1_.substring(0, 9)+o2_.substring(10, 22)+o3_.substring(22);
    }
    
  function gh(f1_, f2_, f3_){
        return f1_.substring(0, 3)+f2_.substring(4, 11)+f3_.substring(10);
    }
    function makerandomString(lenght) {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < lenght; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }
}