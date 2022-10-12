#!/usr/bin/env zsh

#This loads all properties from the file into variables.
#The period is replaced with an underscore, thus my.property will become $my_property

fileName="config.properties"

if [ ! -f "${fileName}" ]; then
  echo "${fileName} not found!"
  return 1
fi

while IFS='=' read -r origKey value; do
  local key=${origKey}
  key=${key//[!a-zA-Z0-9_]/_}
  if [[ "${origKey}" == "#"*   ]]; then
    local ignoreComments
  elif [ -z "${key}" ]; then
    local emptyLine
  else
    if [[ "${key}" =~ ^[0-9].* ]]; then
      key=_${key}
    fi
    eval ${key}=\${value}
  fi
done < <(grep "" ${fileName})