#!/bin/bash
set -e

#fSizes=('1Mb' '10Mb' '100Mb')
fSizes=('1Mb')

nFiles=(10 100 500)

for fSize in ${fSizes[*]};
    do

        wget http://speedtest.ftp.otenet.gr/files/test$fSize.db

        for nFile in ${nFiles[*]};
            do

                mkdir $nFile'f-'$fSize

                for (( i=1; i<= nFile; i++ ));
                    do
                        cp "test$fSize.db" $nFile'f-'$fSize'/'test$fSize$i.db
                    done

            done

        rm -f test$fSize.db

    done

echo "Jobs done!"

