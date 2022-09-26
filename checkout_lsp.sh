#!/bin/bash
cd dependency-build

if [ ! -d lsp4intellij ]; then
  git clone https://github.com/gayanper/lsp4intellij.git && git -C ./lsp4intellij switch -c build-branch remotes/origin/build-branch
fi

cd ../