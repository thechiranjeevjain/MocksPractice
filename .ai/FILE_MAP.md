# File Map

Load the smallest tier that answers the current operation.

## Tier 0 - always

- `.ai/OPERATOR_QUICKCARD.md`
- `.interviewer/INTERVIEWER_STATE.md`
- `.interviewer/SESSION_HISTORY.md`

## Tier 1 - mock planning

- `.ai/INTERVIEW_AGENT.md`
- `.interviewer/CANDIDATE_MODEL.md`
- `.interviewer/MASTERY_MATRIX.md`
- `.interviewer/TOPIC_COVERAGE.md`
- `.interviewer/FAILURE_LOG.md`
- `.interviewer/PARAMETERS.json`
- `.interviewer/DOSSIER_STATUS.md`
- bounded cache `search`/`catalog` results

## Tier 2 - grading and adaptation

- active session submission
- `.interviewer/EVIDENCE_LOG.md`
- `.interviewer/PROGRESS.md`
- `.interviewer/SELF_MODEL.md`
- recent completed session reviews only

## Tier 3 - maintenance

- `config/repositories.json`
- `.interviewer/snapshots/`
- `.interviewer/SOURCE_SYNC_LOG.md`
- `.interviewer/TUNING_LOG.md`
- `.interviewer/archive/`
- `cache/README.md`

Never bulk-load repositories when cache search can retrieve bounded evidence.
