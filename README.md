# EANM
Simple settings manager for EVE. Set up your settings on one character, then use EANM to reflect your changes on all your characters !

# Installation
1. Download jar file (https://github.com/Bertral/EANM/raw/master/out/artifacts/EANM_jar/EANM.jar) or build it from source (this is an IDEA project).
2. Locate your settings directory (which should be in /users/USERNAME/Local Settings/Application Data/CCP/EVE/c_tq_tranquility/ or similar)
3. Place EANM.jar inside the settings directory, where all your "core_char_XXXXXX.dat" are.

## Linux

As this application is written in Java, you need a Java runtime installed. From a terminal, you can run `which java` or `java -version` to see if you have a Java runtime available. If not, most modern Linux distributions have OpenJDK in their system package managers.

Settings directory for CCP's Linux client: `~/.eve/wineenv/drive_c/users/USERNAME/Local Settings/Application Data/CCP/EVE/c_tq_tranquility/settings_Default`

### Arch

```
$ sudo pacman -S jre8-openjdk-headless
```

### Ubuntu & Debian

```
$ sudo apt install openjdk-8-jre
```

### Fedora

```
$ sudo dnf install java-1.8.0-openjdk-headless
```

# Usage
0. It is highly recommended that you backup your settings before using EANM.
1. Chose which account and which character will be used to override the settings of every other characters and accounts.
2. Chose whether you want character specific, account specific, or all settings to be overwitten.
3. Click "Overwrite".

## Linux Usage

The above usage instructions can be followed, but to execute the JAR file, you need to do:

```
$ cd ~/.eve/wineenv/drive_c/users/USERNAME/Local Settings/Application Data/CCP/EVE/c_tq_tranquility/settings_Default
$ java -jar EANM.jar
```

# Troubleshooting

### Double clicking EANM.jar does nothing or briefly shows a terminal window (windows)

This usually means windows is trying to execute the jar file with the wrong executable, even when going through the "open with..." dialog. Make sure `javaw.exe` (not `java.exe`) is default to open jar files. Windows can be stubborn and not let you fix it, so using a third party tool like [Jarfix](https://jarfix.en.softonic.com/) might be the only way to set `javaw.exe` as default for jar files.

# Alternative
[Tau Cabalander explains a way to link all settings to a master file, so that all your characters share the same files at all times.](https://forums-archive.eveonline.com/message/6802475/#post6802475)

[MintNick has developped an alternative with more features.](https://github.com/mintnick/eve-settings-manager)
