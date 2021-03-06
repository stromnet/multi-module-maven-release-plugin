package com.github.danielflower.mavenplugins.release;

import org.junit.Assert;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static scaffolding.ReleasableModuleBuilder.aModule;

public class ReactorTest {

    @Test
    public void canFindModulesByGroupAndArtifactName() throws Exception {
        ReleasableModule arty = aModule().withGroupId("my.great.group").withArtifactId("some-arty").build();
        Reactor reactor = new Reactor(asList(
            aModule().build(), arty, aModule().build()
        ));
        assertThat(reactor.find("my.great.group", "some-arty", "1.0-SNAPSHOT"), is(arty));
        assertThat(reactor.findByLabel("my.great.group:some-arty"), is(arty));
    }

    @Test
    public void findOrReturnNullReturnsNullIfNotFound() throws Exception {
        Reactor reactor = new Reactor(asList(
            aModule().build(), aModule().build()
        ));
        assertThat(reactor.findByLabel("my.great.group:some-arty"), is(nullValue()));
    }

    @Test
    public void ifNotFoundThenAUnresolvedSnapshotDependencyExceptionIsThrown() throws Exception {
        Reactor reactor = new Reactor(asList(
            aModule().build(), aModule().build()
        ));
        try {
            reactor.find("my.great.group", "some-arty", "1.0-SNAPSHOT");
            Assert.fail("Should have thrown");
        } catch (UnresolvedSnapshotDependencyException e) {
            assertThat(e.getMessage(), equalTo("Could not find my.great.group:some-arty:1.0-SNAPSHOT"));
        }
    }
}
