# Cordova Tottems File
HTML source file is encrypted at build, and decrypted at run.  
https://www.npmjs.com/package/cordova-plugin-tottems

## Add Plugin
`cordova plugin add cordova-plugin-tottems`

## Remove Plugin
`cordova plugin rm cordova-plugin-tottems`

## Encrypt
`cordova build [android]`



### Edit subjects

You can specify the encryption subjects by editing `plugin.xml`.

**plugins/cordova-plugin-tottems/plugin.xml**

```
<cryptfiles>
    <include>
        <file regex="\.(htm|html|js|css)$" />
    </include>
    <exclude>
        <file regex="exclude_file\.js$" />
    </exclude>
</cryptfiles>
```

Specify the target file as a regular expression.


## Supported platforms

* Android

