<?php

\DB::query()->from('users')->paginate(15, ['<caret>']);
