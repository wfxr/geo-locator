#!/usr/bin/env bash
################################################################################
#    Author: Wenxuan                                                           #
#     Email: wenxuangm@gmail.com                                               #
#   Created: 2018-09-04 11:15                                                  #
################################################################################
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd) && cd "$SCRIPT_DIR" || exit 1

source .env

mkdir -p districts

while IFS= read -r code; do
    curl -Ss "https://restapi.amap.com/v3/config/district?keywords=$code&subdistrict=0&key=$GAODE_KEY&extensions=all" |
        jq '.districts[0]' > "districts/$code.json"
    echo "$code done."
done < "./adcode"
