# Issues

If you encounter an issue with UbiCrypt, you are welcome to submit a [bug report](https://github.com/gfrison/ubicrypt/issues).
Before that, please search for similar issues. It's possible somebody has encountered this issue already.

# Pull Requests

If you want to contribute to the repository, here's a quick guide:
  1. Fork the repository
  2. develop and test your code changes, gradle: `gradle test`.
    * Respect the original code [style guide][styleguide].
    * Create minimal diffs - disable on save actions like reformat source code or organize imports. If you feel the source code should be reformatted create a separate PR for this change.
    * Check for unnecessary whitespace with git diff --check before committing.
  3. Make the test pass
  4. Commit your changes:   
    * Use the present tense (`"Add feature"` not `"Added Feature"`)
    * Use the imperative mood (`"Move cursor to…"` not `"Moves cursor to…"`)
    * Include relevant Emoji from our [Emoji cheatsheet](#emoji-cheatsheet)
  5. Push to your fork and submit a pull request to the **master** branch
  
  Example git comment:
  ````
      (Issue-ID) Make the example in CONTRIBUTING imperative and concrete
  
      Without this patch applied the example commit message in the CONTRIBUTING
      document is not a concrete example.  This is a problem because the
      contributor is left to imagine what the commit message should look like
      based on a description rather than an example.  This patch fixes the
      problem by making the example concrete and imperative.
  
      The first line is a real life imperative statement with a ticket number
      from our issue tracker.  The body describes the behavior without the patch,
      why this is a problem, and how the patch fixes the problem when applied.
  ````
  
  For changes of a trivial nature to comments and documentation, it is not
  always necessary to create a new ticket in Jira. In this case, it is
  appropriate to start the first line of a commit with '(doc)' instead of
  a ticket number.
  
  ````
      (doc) Add documentation commit example to CONTRIBUTING
  
      There is no example for contributing a documentation commit
      to the Puppet repository. This is a problem because the contributor
      is left to assume how a commit of this nature may appear.
  
      The first line is a real life imperative statement with '(doc)' in
      place of what would have been the ticket number in a
      non-documentation related commit. The body describes the nature of
      the new documentation or comments added.
  ````

# Developer's Certificate of Origin 1.1

By making a contribution to this project, I certify that:

(a) The contribution was created in whole or in part by me and I
   have the right to submit it under the license
   indicated in the file; or

(b) The contribution is based upon previous work that, to the best
   of my knowledge, is covered under an appropriate 
   license and I have the right under that license to submit that
   work with modifications, whether created in whole or in part
   by me, under the same license (unless I am
   permitted to submit under a different license), as indicated
   in the file; or

(c) The contribution was provided directly to me by some other
   person who certified (a), (b) or (c) and I have not modified
   it.

(d) I understand and agree that this project and the contribution
   are public and that a record of the contribution (including all
   personal information I submit with it, including my sign-off) is
   maintained indefinitely and may be redistributed consistent with
   this project's license.


## Additional Resources
+ [General GitHub documentation](https://help.github.com/)
+ [GitHub pull request documentation](https://help.github.com/send-pull-requests/)

[styleguide]: https://google.github.io/styleguide/javaguide.html
[Gradle]: https://docs.gradle.org/current/userguide/installation.html
