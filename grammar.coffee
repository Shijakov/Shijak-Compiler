PROGRAM ::= DEFINITION FUNCTION_OR_BAG
PROGRAM ::= FUNCTION_OR_BAG

DEFINITION ::= define { DEFINITION_ASSIGNMENT }
DEFINITION_ASSIGNMENT ::= PRIMITIVE_CONSTANT >> identifier ; DEFINITION_ASSIGNMENT
DEFINITION_ASSIGNMENT ::= ''

FUNCTION_OR_BAG ::= FUNCTION FUNCTION_OR_BAG
FUNCTION_OR_BAG ::= BAG_DEFINITION FUNCTION_OR_BAG
FUNCTION_OR_BAG ::= ''

BAG_DEFINITION ::= bag identifier { BAG_DEFINITION_PARAMETER_LIST }

BAG_DEFINITION_PARAMETER_LIST ::= identifier : TYPE BAG_DEFINITION_PARAMETER_LIST_TAIL
BAG_DEFINITION_PARAMETER_LIST_TAIL ::= , identifier : TYPE BAG_DEFINITION_PARAMETER_LIST_TAIL
BAG_DEFINITION_PARAMETER_LIST_TAIL ::= ''

FUNCTION ::= fun identifier ( PARAM_LIST ) : RETURN_TYPE { STATEMENT_LIST }

PARAM_LIST ::= identifier : TYPE PARAM_LIST_TAIL
PARAM_LIST ::= ''
PARAM_LIST_TAIL ::= , identifier : TYPE PARAM_LIST_TAIL
PARAM_LIST_TAIL ::= ''

STATEMENT_LIST ::= STATEMENT STATEMENT_LIST
STATEMENT_LIST ::= ''

STATEMENT ::= WHILE_STATEMENT
STATEMENT ::= IF_STATEMENT
STATEMENT ::= BREAK_STATEMENT
STATEMENT ::= CONTINUE_STATEMENT
STATEMENT ::= EXPRESSION_LIST
STATEMENT ::= DEFINE_VAR
STATEMENT ::= INPUT_STATEMENT
STATEMENT ::= OUTPUT_STATEMENT
STATEMENT ::= ALLOC_ARR
STATEMENT ::= FREE_ARR
STATEMENT ::= RETURN_STATEMENT
STATEMENT ::= FILL_STATEMENT

FILL_STATEMENT ::= fill bag identifier { BAG_PARAMETER_LIST } >> ASSIGNABLE_INSTANCE ;

BAG_PARAMETER_LIST ::= identifier : EXPRESSION BAG_PARAMETER_LIST_TAIL
BAG_PARAMETER_LIST ::= ''

BAG_PARAMETER_LIST_TAIL ::= , identifier : EXPRESSION BAG_PARAMETER_LIST_TAIL
BAG_PARAMETER_LIST_TAIL ::= ''

DEFINE_VAR ::= let identifier : TYPE ;

ALLOC_ARR ::= alloc ALLOC_ARR_TYPE >> ASSIGNABLE_INSTANCE ;
FREE_ARR ::= free ASSIGNABLE_INSTANCE ;

RETURN_STATEMENT ::= return ;

BREAK_STATEMENT ::= break ;

CONTINUE_STATEMENT ::= continue ;

INPUT_STATEMENT ::= input ASSIGNABLE_INSTANCE ;

OUTPUT_STATEMENT ::= output EXPRESSION ;

WHILE_STATEMENT ::= while ( EXPRESSION ) { STATEMENT_LIST }

IF_STATEMENT ::= if ( EXPRESSION ) { STATEMENT_LIST } ELIF_STATEMENT ELSE_STATEMENT
ELIF_STATEMENT ::= elif ( EXPRESSION ) { STATEMENT_LIST } ELIF_STATEMENT
ELIF_STATEMENT ::= ''
ELSE_STATEMENT ::= else { STATEMENT_LIST }
ELSE_STATEMENT ::= ''

EXPRESSION_LIST ::= EXPRESSION EXPRESSION_LIST_TAIL ;

EXPRESSION_LIST_TAIL ::= >> EXPRESSION_OR_CLOSER
EXPRESSION_LIST_TAIL ::= ''

EXPRESSION_OR_CLOSER ::= CLOSER
EXPRESSION_OR_CLOSER ::= EXPRESSION EXPRESSION_LIST_TAIL

CLOSER ::= return
CLOSER ::= eq ASSIGNABLE_INSTANCE

EXPRESSION ::= MOD_EXPR EXPRESSION_TAIL
EXPRESSION_TAIL ::= >= MOD_EXPR
EXPRESSION_TAIL ::= <= MOD_EXPR
EXPRESSION_TAIL ::= < MOD_EXPR
EXPRESSION_TAIL ::= > MOD_EXPR
EXPRESSION_TAIL ::= == MOD_EXPR
EXPRESSION_TAIL ::= != MOD_EXPR
EXPRESSION_TAIL ::= ''

MOD_EXPR ::= ADD_EXPR MOD_EXPR_TAIL
MOD_EXPR_TAIL ::= % ADD_EXPR MOD_EXPR_TAIL
MOD_EXPR_TAIL ::= ''

ADD_EXPR ::= MUL_EXPR ADD_EXPR_TAIL
ADD_EXPR_TAIL ::= + MUL_EXPR ADD_EXPR_TAIL
ADD_EXPR_TAIL ::= - MUL_EXPR ADD_EXPR_TAIL
ADD_EXPR_TAIL ::= || MUL_EXPR ADD_EXPR_TAIL
ADD_EXPR_TAIL ::= ''

MUL_EXPR ::= PRIMARY MUL_EXPR_TAIL
MUL_EXPR_TAIL ::= * PRIMARY MUL_EXPR_TAIL
MUL_EXPR_TAIL ::= / PRIMARY MUL_EXPR_TAIL
MUL_EXPR_TAIL ::= && PRIMARY MUL_EXPR_TAIL
MUL_EXPR_TAIL ::= ''

PRIMARY ::= INSTANCE
PRIMARY ::= ! PRIMARY
PRIMARY ::= ( EXPRESSION )
PRIMARY ::= PRIMITIVE_CONSTANT
PRIMARY ::= in

INSTANCE ::= identifier INSTANCE_TAIL
INSTANCE_TAIL ::= ( EXPR_LIST )
INSTANCE_TAIL ::= ASSIGNABLE_INSTANCE_TAIL

ASSIGNABLE_INSTANCE ::= identifier ASSIGNABLE_INSTANCE_TAIL

ASSIGNABLE_INSTANCE_TAIL ::= [ MOD_EXPR ] DOT_TAIL
ASSIGNABLE_INSTANCE_TAIL ::= DOT_TAIL

DOT_TAIL ::= . identifier ASSIGNABLE_INSTANCE_TAIL
DOT_TAIL ::= ''

EXPR_LIST ::= EXPRESSION EXPR_LIST_TAIL 
EXPR_LIST ::= ''
EXPR_LIST_TAIL ::= , EXPRESSION EXPR_LIST_TAIL
EXPR_LIST_TAIL ::= ''

RETURN_TYPE ::= TYPE
RETURN_TYPE ::= void

ALLOC_ARR_TYPE ::= PRIMITIVE_TYPE [ MOD_EXPR ]

TYPE ::= PRIMITIVE_TYPE ARRAY_EMPTY_EXTENSION

ARRAY_EMPTY_EXTENSION ::= [ ]
ARRAY_EMPTY_EXTENSION ::= ''

PRIMITIVE_TYPE ::= int
PRIMITIVE_TYPE ::= float
PRIMITIVE_TYPE ::= char
PRIMITIVE_TYPE ::= bool
PRIMITIVE_TYPE ::= bag identifier

PRIMITIVE_CONSTANT ::= char_constant
PRIMITIVE_CONSTANT ::= bool_constant
PRIMITIVE_CONSTANT ::= int_constant
PRIMITIVE_CONSTANT ::= float_constant
