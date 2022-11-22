/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010-2022 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.flex;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.impl.Lexer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.flex.FlexVisitor;
import org.sonar.flex.api.FlexKeyword;
import org.sonar.flex.api.FlexPunctuator;
import org.sonar.flex.api.FlexTokenType;

public class FlexTokensVisitor extends FlexVisitor {

  private static final String NORMALIZED_CHARACTER_LITERAL = "$CHARS";
  private static final String NORMALIZED_NUMERIC_LITERAL = "$NUMBER";
  private static final Set<FlexKeyword> KEYWORDS = EnumSet.allOf(FlexKeyword.class);

  private final SensorContext context;
  private final Lexer lexer;
  private final InputFile inputFile;

  public FlexTokensVisitor(SensorContext context, Lexer lexer, InputFile inputFile) {
    this.context = context;
    this.lexer = lexer;
    this.inputFile = inputFile;
  }

  @Override
  public List<AstNodeType> subscribedTo() {
    return Collections.emptyList();
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    NewHighlighting highlighting = context.newHighlighting();
    highlighting.onFile(inputFile);

    NewCpdTokens cpdTokens = context.newCpdTokens();
    cpdTokens.onFile(inputFile);

    Iterator<Token> iterator = lexer.lex(getContext().fileContent()).iterator();
    // we currently use this hack to remove "import" directives
    boolean importDirective = false;
    while (iterator.hasNext()) {
      Token token = iterator.next();
      TokenType tokenType = token.getType();
      if (tokenType.equals(FlexKeyword.IMPORT)) {
        importDirective = true;
      } else if (importDirective) {
        // We do nothing as we want to ignore "import" directives
        if (tokenType.equals(FlexPunctuator.SEMI)) {
          importDirective = false;
        }
      } else if (!tokenType.equals(GenericTokenType.EOF)) {
        TokenLocation tokenLocation = new TokenLocation(token);
        cpdTokens.addToken(tokenLocation.startLine(), tokenLocation.startCharacter(), tokenLocation.endLine(), tokenLocation.endCharacter(), getTokenImage(token));
      }
      if (tokenType.equals(FlexTokenType.NUMERIC_LITERAL)) {
        highlight(highlighting, token, TypeOfText.CONSTANT);
      } else if (tokenType.equals(GenericTokenType.LITERAL)) {
        highlight(highlighting, token, TypeOfText.STRING);
      } else if (KEYWORDS.contains(tokenType)) {
        highlight(highlighting, token, TypeOfText.KEYWORD);
      }
      for (Trivia trivia : token.getTrivia()) {
        highlight(highlighting, trivia.getToken(), TypeOfText.COMMENT);
      }
    }

    highlighting.save();
    cpdTokens.save();
  }

  private static String getTokenImage(Token token) {
    if (token.getType().equals(GenericTokenType.LITERAL)) {
      return NORMALIZED_CHARACTER_LITERAL;
    } else if (token.getType().equals(FlexTokenType.NUMERIC_LITERAL)) {
      return NORMALIZED_NUMERIC_LITERAL;
    }
    return token.getValue();
  }

  private static void highlight(NewHighlighting highlighting, Token token, TypeOfText typeOfText) {
    TokenLocation tokenLocation = new TokenLocation(token);
    highlighting.highlight(tokenLocation.startLine(), tokenLocation.startCharacter(), tokenLocation.endLine(), tokenLocation.endCharacter(), typeOfText);
  }

}
