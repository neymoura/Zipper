#!/bin/bash
set -e

#fSizes=('1Mb' '10Mb' '100Mb')
#fSizes=('1Mb' '10Mb')
fSizes=('1Mb')

nFiles=(10 100 500)

echo "[smart-downloader] Starting..."

for fSize in ${fSizes[*]};
    do

        if [ -f "test$fSize.db" ]; then
            echo "[smart-downloader] File exists! Skipping download!"
        else
            echo "[smart-downloader] Downloading $fSize file..."
            wget http://speedtest.ftp.otenet.gr/files/test$fSize.db
        fi

        for nFile in ${nFiles[*]};
            do

                if [ -d $nFile"f-"$fSize ]; then
                    echo "[smart-downloader] Directory exists! Skipping creation!"
                else
                    echo "[smart-downloader] Creating dir $nFile'f-'$fSize..."
                    mkdir $nFile'f-'$fSize
                fi

                for (( i=1; i<= nFile; i++ ));
                    do

                        if [ -f $nFile"f-"$fSize"/"test$fSize$i.db ]; then
                            echo "[smart-downloader] File exists! Skipping copy!"
                        else
                            echo "[smart-downloader] Copying test$fSize.db into" $nFile'f-'$fSize'/'test$fSize$i.db
                            cp "test$fSize.db" $nFile'f-'$fSize'/'test$fSize$i.db
                        fi

                    done

            done

        if [ -f "test$fSize.db" ]; then
            rm -f test$fSize.db
        fi

    done

echo "[smart-downloader] Jobs done!"

