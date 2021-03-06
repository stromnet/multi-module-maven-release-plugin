package com.github.danielflower.mavenplugins.release;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class DiffDetector {

    private final Repository repo;

    public DiffDetector(Repository repo) {
        this.repo = repo;
    }

    public boolean hasChangedSince(String modulePath, java.util.List<String> childModules, Collection<AnnotatedTag> tags) throws IOException {
        RevWalk walk = new RevWalk(repo);
        try {
            walk.setRetainBody(false);
            walk.markStart(walk.parseCommit(repo.getRef("HEAD").getObjectId()));

            TreeFilter treeFilter;
            if (".".equals(modulePath) && childModules.size() == 0) {
                treeFilter = TreeFilter.ANY_DIFF;
            } else {
                Collection<TreeFilter> treeFilters = new ArrayList<TreeFilter>();
                treeFilters.add(PathFilter.create(modulePath + "/"));
                treeFilters.add(TreeFilter.ANY_DIFF);
                for (String childModule : childModules) {
                    treeFilters.add(PathFilter.create(modulePath + "/" + childModule).negate());
                }
                treeFilter = AndTreeFilter.create(treeFilters);
            }
            walk.setTreeFilter(treeFilter);

            for (AnnotatedTag tag : tags) {
                ObjectId commitId = tag.ref().getTarget().getObjectId();
                RevCommit revCommit = walk.parseCommit(commitId);
                walk.markUninteresting(revCommit);
            }

            return walk.iterator().hasNext();
        } finally {
            walk.dispose();
        }
    }
}
