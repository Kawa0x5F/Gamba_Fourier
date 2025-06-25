ANT = env LC_ALL=ja_JP.UTF-8 ant
ARCHIVE = $(shell basename `pwd`)
SOURCES = $(shell find . -name "*.java")
STYLE_YAML  = clang-format-for-java.yaml
STYLE_CONF  = _clang-format

# .PHONY ディレクティブを追加し、アクションを表すターゲットをすべて指定します。
.PHONY: all clean test package doc wipe zip format app check if ifTrue ifFalse ifThenElse while whileTrue whileFalse for forEach

all:
	$(ANT) all

clean:
	$(ANT) clean

test:
	$(ANT) test

package:
	$(ANT) package

doc:
	$(ANT) doc

wipe: clean
	@find . -name ".DS_Store" -exec rm {} ";" -exec echo rm -f {} ";"
	(cd ../ ; rm -f ./$(ARCHIVE).zip)

#zip:
#	$(ANT) zip

zip: wipe
	@find . -exec touch -t `date "+%Y%m%d%H%M"` {} \; ; xattr -cr .
	(cd ../ ; zip -r ./$(ARCHIVE).zip ./$(ARCHIVE)/ --exclude='*/.svn/*')

format:
	@rm -f $(STYLE_CONF) ; ln -s $(STYLE_YAML) $(STYLE_CONF)
	for each in $(SOURCES) ; do echo ---[$${each}]--- ; clang-format -style=file $${each} ; echo ; done
	@rm -f $(STYLE_CONF)

app: package
	@xattr -cr ./build/Fourier.app
	open ./build/Fourier.app

check: if while for ifTrue ifFalse ifThenElse whileTrue whileFalse forEach
	@:

if:
	@echo "---<" $@ ">---" 
	@find . -name '*.java' -exec grep -HnE 'if[\ \t]*\(' {} \; | sort

ifTrue:
	@echo "---<" $@ ">---" 
	@find . -name '*.java' -exec grep -HnE 'ifTrue[\ \t]*\(' {} \; | sort

ifFalse:
	@echo "---<" $@ ">---" 
	@find . -name '*.java' -exec grep -HnE 'ifFalse[\ \t]*\(' {} \; | sort

ifThenElse:
	@echo "---<" $@ ">---" 
	@find . -name '*.java' -exec grep -HnE 'ifThenElse[\ \t]*\(' {} \; | sort

while:
	@echo "---<" $@ ">---" 
	@find . -name '*.java' -exec grep -HnE 'while[\ \t]*\(' {} \; | sort

whileTrue:
	@echo "---<" $@ ">---" 
	@find . -name '*.java' -exec grep -HnE 'whileTrue[\ \t]*\(' {} \; | sort

whileFalse:
	@echo "---<" $@ ">---" 
	@find . -name '*.java' -exec grep -HnE 'whileFalse[\ \t]*\(' {} \; | sort

for:
	@echo "---<" $@ ">---" 
	@find . -name '*.java' -exec grep -HnE 'for[\ \t]*\(' {} \; | sort

forEach:
	@echo "---<" $@ ">---" 
	@find . -name '*.java' -exec grep -HnE 'forEach[\ \t]*\(' {} \; | sort

