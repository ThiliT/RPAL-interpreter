# Compiler
JAVAC = javac

# Source directory
SRC_DIR = src

# Output directory
OUT_DIR = $(SRC_DIR)

CSEM_DIR = CSEmachine
STANDARDIZE_DIR = Standardize
PARSER_DIR = parser
SCANNER_DIR = scanner

# Java source files
JAVA_FILES := $(wildcard $(SRC_DIR)/**/*.java) \
              $(wildcard $(CSEM_DIR)/*.java) \
              $(wildcard $(STANDARDIZE_DIR)/*.java) \
              $(wildcard $(PARSER_DIR)/*.java) \
              $(wildcard $(SCANNER_DIR)/*.java) \
              $(wildcard $(SRC_DIR)/myrpal.java)

# Classpath
CLASSPATH = $(SRC_DIR)

# Main class
MAIN_CLASS = myrpal

# Targets
.PHONY: all clean

all: $(JAVA_FILES:.java=.class)

clean:
	@find . -name "*.class" -type f -delete

# Compilation rule
$(SRC_DIR)/%.class: $(SRC_DIR)/%.java
	$(JAVAC) -classpath $(CLASSPATH) -d $(OUT_DIR) $<

# Run target
run: all
	@java -cp $(OUT_DIR) $(MAIN_CLASS)
