#!/bin/bash
set -e

#fSizes=('1Mb' '10Mb' '100Mb')
fSizes=('1Mb' '10Mb')
#fSizes=('1Mb')

nFiles=(10 100 200 300)
#nFiles=(10 100)
#nFiles=(10)

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


#10 100 20

base64 /dev/urandom | head -c 1000000 > file1Mb.txt #1mb

base64 /dev/urandom | head -c 10000000 > file10Mb.txt #10mb

#base64 /dev/urandom | head -c 100000000 > file100Mb.txt #100mb

for nFile in ${nFiles[*]};
    do

#################### 1 Mb ####################

        if [ -d $nFile"f-1Mb-txt" ]; then
            rm -rf $nFile"f-1Mb-txt"
        fi

        echo "[smart-downloader] Creating dir $nFile'f-1Mb-txt..."
        mkdir $nFile"f-1Mb-txt"

        for (( i=1; i<= nFile; i++ ));
            do
                cp "file1Mb.txt" $nFile"f-1Mb-txt/file1Mb"$i".txt"
            done

#################### 10 Mb ####################

        if [ -d $nFile"f-10Mb-txt" ]; then
            rm -rf $nFile"f-10Mb-txt"
        fi

        echo "[smart-downloader] Creating dir $nFile'f-10Mb-txt..."
        mkdir $nFile"f-10Mb-txt"

        for (( i=1; i<= nFile; i++ ));
            do
                cp "file10Mb.txt" $nFile"f-10Mb-txt/file10Mb"$i".txt"
            done

#################### 100 Mb ####################
#
#        if [ -d $nFile"f-100Mb-txt" ]; then
#            rm -rf $nFile"f-100Mb-txt"
#        fi
#
#        echo "[smart-downloader] Creating dir $nFile'f-100Mb-txt..."
#        mkdir $nFile"f-100Mb-txt"
#
#        for (( i=1; i<= nFile; i++ ));
#            do
#                cp "file100Mb.txt" $nFile"f-100Mb-txt/file100Mb"$i".txt"
#            done
#
#base64 /dev/urandom | head -c 100000000 > file.txt #100mb
#base64 /dev/urandom | head -c 10000000 > file.txt #10mb
#base64 /dev/urandom | head -c 1000000 > file.txt #1mb

    done

echo "[smart-downloader] Jobs done!"