package io.berndruecker.playground.zeebe.tests.twitter;


/**
 * Publish content on Twitter.
 */
public interface TwitterService {

  void tweet(String content) throws DuplicateTweetException;

}
