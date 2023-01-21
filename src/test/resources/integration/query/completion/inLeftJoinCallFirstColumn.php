<?php

\DB::query()->from('users')->leftJoin('customers', '<caret>');
