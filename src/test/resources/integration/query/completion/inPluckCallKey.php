<?php

\DB::query()->from('users')->pluck('first_name', '<caret>');
