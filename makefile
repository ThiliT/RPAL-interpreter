JC = javac

# Directories
SRC_DIR = .
OUTPUT_DIR = .
CSEM_DIR = CSEmachine
STANDARDIZE_DIR = Standardize
PARSER_DIR = parser
SCANNER_DIR = scanner

JAVA_FILES := $(wildcard $(SRC_DIR)/*.java) \
              $(wildcard $(CSEM_DIR)/*.java) \
              $(wildcard $(STANDARDIZE_DIR)/*.java) \
              $(wildcard $(PARSER_DIR)/*.java) \
              $(wildcard $(SCANNER_DIR)/*.java)

OBJ_FILES := $(patsubst %.java, $(OUTPUT_DIR)/%.class, $(JAVA_FILES))

all: $(OBJ_FILES)

# Compile
$(OUTPUT_DIR)/%.class: %.java
	$(JC) -d $(OUTPUT_DIR) $<

# Clean build
clean:
	find . -name "*.class" -type f -delete

.PHONY: all clean