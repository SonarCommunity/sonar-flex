/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010-2018 SonarSource SA
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
package org.sonar.flex.checks;

import com.google.common.io.Files;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.Parser;
import com.sonarsource.checks.verifier.CommentParser;
import com.sonarsource.checks.verifier.SingleFileVerifier;
import java.io.File;
import java.io.IOException;
import org.sonar.flex.FlexCheck;
import org.sonar.flex.FlexConfiguration;
import org.sonar.flex.FlexVisitorContext;
import org.sonar.flex.Issue;
import org.sonar.flex.parser.FlexParser;
import org.sonar.sslr.parser.LexerlessGrammar;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FlexVerifier {

  public static void verify(File file, FlexCheck check) {
    createVerifier(file, check, true).assertOneOrMoreIssues();
  }

  public static void verifyNoIssue(File file, FlexCheck check) {
    createVerifier(file, check, true).assertNoIssues();
  }

  public static void verifyNoIssueIgnoringExpected(File file, FlexCheck check) {
    createVerifier(file, check, false).assertNoIssues();
  }

  private static SingleFileVerifier createVerifier(File file, FlexCheck check, boolean addCommentsAsExpectedIssues) {
    SingleFileVerifier verifier = SingleFileVerifier.create(file.toPath(), UTF_8);

    FlexVisitorContext context = createContext(file);

    for (Issue issue : check.scanFileForIssues(context)) {
      SingleFileVerifier.IssueBuilder issueBuilder = verifier.reportIssue(issue.message());
      Integer line = issue.line();
      SingleFileVerifier.Issue verifierIssue;
      if (line != null) {
        verifierIssue = issueBuilder.onLine(line);
      } else {
        verifierIssue = issueBuilder.onFile();
      }
      verifierIssue.withGap(issue.cost());
    }

    if (addCommentsAsExpectedIssues) {
      CommentParser commentParser = CommentParser.create().addSingleLineCommentSyntax("//");
      commentParser.parseInto(file.toPath(), verifier);
    }

    return verifier;
  }

  public static FlexVisitorContext createContext(File file) {
    Parser<LexerlessGrammar> parser = FlexParser.create(new FlexConfiguration(UTF_8));
    String fileContent;
    try {
      fileContent = Files.toString(file, UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read " + file, e);
    }
    FlexVisitorContext context;
    try {
      context = new FlexVisitorContext(fileContent, parser.parse(file));
    } catch (RecognitionException e) {
      context = new FlexVisitorContext(fileContent, e);
    }
    return context;
  }

}
