/*
 * Copyright (C) 2023-2024 ConnectorIO Sp. z o.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
grammar Things;

// This grammar file describes both lexer and parser rules needed to parse openHAB's things files.
// It has some touch base with xtext, however it drives some parts further in making them pluggable.
// First of all, it does allow dynamic item types which are not static
// The lexer also differentiaties INTEGER and DECIMAL types during scanning for better error reporting.

things
  : (modelThing | modelBridge)* EOF
  ;

modelBridge
  :
  BRIDGE id=uid
  label=STRING?
  parent=modelParent?
  bridge=bridgeDefinition
  ;

nestedBridge
  :
  BRIDGE thingTypeId=uidSegment id=uidSegment
  label=STRING?
  bridge=bridgeDefinition
  ;

bridgeDefinition
  :
  (LOCATION location=STRING)?
  properties=modelProperties?
  (
    L_BRACE
    nestedThings
    modelChannels
    R_BRACE
  )?
  ;

nestedThings
  : THINGS? ( nestedThing | nestedBridge )*
  ;

modelThing
  :
  THING id=uid
  label=STRING?
  parent=modelParent?
  thing=thingDefinition
  ;

nestedThing
  :
  THING thingTypeId=uidSegment id=uidSegment
  label=STRING?
  thing=thingDefinition
  ;

thingDefinition
  :
  (LOCATION location=STRING)?
  properties=modelProperties?
  (
    L_BRACE
    modelChannels
    R_BRACE
  )?
  ;

modelParent
  : L_PAREN id=uid R_PAREN
  ;

modelChannels
  : CHANNELS? modelChannel*
  ;

modelChannel
  : (channelDeclaration | channelReference) ':' id=channelId (label=STRING)? properties=modelProperties?
  ;

channelDeclaration
  : (channelKind=CHANNEL_KIND)? itemType=modelItemType
  ;

channelReference
  : TYPE channelType=uidSegment
  ;

modelItemType
  : standardItemType
  | dimensionalItemType
  | dynamicItemType
  ;

dimensionalItemType
  : 'Number:' ID
  ;

// Item type resolved at runtime, might be outside of "core" item types defined by openHAB itself.
dynamicItemType
  : ID
  ;

standardItemType
  : 'Switch'
  | 'Rollershutter'
  | 'Number'
  | 'String'
  | 'Dimmer'
  | 'Contact'
  | 'DateTime'
  | 'Color'
  | 'Player'
  | 'Location'
  | 'Call'
  | 'Image'
  ;

modelProperties
  : L_BRACKET properties=modelProperty? (',' properties=modelProperty)* R_BRACKET
  ;

modelProperty
  : key=ID '=' value=valueType (',' value=valueType)*
  ;

valueType
  : STRING
  | NUMBER
  | BOOLEAN
  ;

uid
  : uidSegment ':' uidSegment ':' uidSegment ( ':' uidSegment )*
  ;

channelId
  : uidSegment ('#' uidSegment)?
  ;

uidSegment
  : ID+
  ;

BOOLEAN
  : 'true'
  | 'false'
  ;

NUMBER: DECIMAL | INTEGER;

THING: 'Thing';
THINGS: 'Things:';
BRIDGE: 'Bridge';
CHANNELS: 'Channels:';
L_PAREN: '(';
R_PAREN: ')';
L_BRACE: '{';
R_BRACE: '}';
L_BRACKET: '[';
R_BRACKET: ']';
LOCATION: '@';
TYPE: 'Type';
STATE: 'State';
TRIGGER: 'Trigger';

CHANNEL_KIND
  : TYPE
  | STATE
  ;

MINUS: '-';
STRING
  : '"'  ( '\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|'\''|'\\') | ~('\\'|'"') )* '"' {setText(getText().substring(1, getText().length() - 1));}
  | '\'' ( '\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|'\''|'\\') | ~('\\'|'\'') )* '\''  {setText(getText().substring(1, getText().length() - 1));}
  ;

ID: ('a'..'z'|'A'..'Z'|'_')+ ('a'..'z'|'A'..'Z'|'_'|'-'|'0'..'9')*;

// Make sure that each integer and decimal values have parsable representation
INTEGER: MINUS? DIGIT+;
DECIMAL: MINUS? DIGIT+ '.' DIGIT+;
DIGIT: '0'..'9';

WS: ( ' ' | [ \t\r\n]+ ) -> channel ( HIDDEN ) ;